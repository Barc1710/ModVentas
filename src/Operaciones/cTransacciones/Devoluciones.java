package Operaciones.cTransacciones;

import Conexion.ConexionBD;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Devoluciones extends JFrame {
    private JTextField txtNumeroSerie;
    private JTextField txtClienteDevolucion;
    private JTable tablaProductosVenta;
    private DefaultTableModel modeloTabla;
    private JButton btnDevolucionTotal;
    private JButton btnDevolucionProducto;

    public Devoluciones() {

        setTitle("Gestión de Devoluciones");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lbltitulo = new JLabel("Devoluciones");
        lbltitulo.setFont(new Font("Verdana", Font.BOLD, 20));
        panel.add(lbltitulo);

        // Panel izquierdo
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new GridLayout(13,1,5,5));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Datos de la Venta"));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Número de Serie:"));
        txtNumeroSerie = new JTextField(10);
        panelBusqueda.add(txtNumeroSerie);
        JButton btnBuscarVenta = new JButton("Buscar");
        panelBusqueda.add(btnBuscarVenta);

        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCliente.add(new JLabel("Cliente:"));
        txtClienteDevolucion = new JTextField(25);
        txtClienteDevolucion.setEditable(false);
        panelCliente.add(txtClienteDevolucion);

        panelIzquierdo.add(panelBusqueda);
        panelIzquierdo.add(panelCliente);

        // Panel central
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Productos de la Venta"));

        modeloTabla = new DefaultTableModel(new Object[]{"ID Detalle", "Producto", "Cantidad", "Precio Unitario", "Total"}, 0);
        tablaProductosVenta = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaProductosVenta);
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDevolucionTotal = new JButton("Devolución Total");
        btnDevolucionProducto = new JButton("Devolución Producto");
        panelInferior.add(btnDevolucionProducto);
        panelInferior.add(btnDevolucionTotal);

        // Agregar paneles al frame principal
        add(panel, BorderLayout.NORTH);
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Listeners para botones
        btnBuscarVenta.addActionListener(e -> buscarVenta());
        btnDevolucionTotal.addActionListener(e -> realizarDevolucionTotal());
        btnDevolucionProducto.addActionListener(e -> realizarDevolucionProducto());

        setVisible(true);
    }

    private void buscarVenta() {
        String numeroSerie = txtNumeroSerie.getText();
        if (numeroSerie.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un número de serie.");
            return;
        }

        try (Connection conexion = ConexionBD.conectar()) {
            // Consultar la venta y los detalles relacionados
            String sqlVenta = "SELECT v.id_venta, c.nombres, c.apellidos " +
                    "FROM ventas v " +
                    "JOIN clientes c ON v.cliente_id = c.id " +
                    "WHERE v.numero_serie = ?";
            PreparedStatement psVenta = conexion.prepareStatement(sqlVenta);
            psVenta.setString(1, numeroSerie);
            ResultSet rsVenta = psVenta.executeQuery();

            if (rsVenta.next()) {
                int idVenta = rsVenta.getInt("id_venta");
                String cliente = rsVenta.getString("nombres") + " " + rsVenta.getString("apellidos");
                txtClienteDevolucion.setText(cliente);

                // Consultar los productos de la venta
                modeloTabla.setRowCount(0);
                String sqlDetalles = "SELECT dv.id_detalle, a.nombre, dv.cantidad, dv.precio_unitario, dv.total " +
                        "FROM detalle_ventas dv " +
                        "JOIN articulos a ON dv.articulos_id = a.id " +
                        "WHERE dv.id_venta = ?";
                PreparedStatement psDetalles = conexion.prepareStatement(sqlDetalles);
                psDetalles.setInt(1, idVenta);
                ResultSet rsDetalles = psDetalles.executeQuery();

                while (rsDetalles.next()) {
                    modeloTabla.addRow(new Object[]{
                            rsDetalles.getInt("id_detalle"),
                            rsDetalles.getString("nombre"),
                            rsDetalles.getInt("cantidad"),
                            rsDetalles.getDouble("precio_unitario"),
                            rsDetalles.getDouble("total")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la venta.");
                limpiarFormulario();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar la venta: " + e.getMessage());
        }
    }

    private void realizarDevolucionTotal() {
        String numeroSerie = txtNumeroSerie.getText();
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos para devolver.");
            return;
        }

        try (Connection conexion = ConexionBD.conectar()) {

            String sqlObtenerVentaId = "SELECT id_venta FROM ventas WHERE numero_serie = ?";
            PreparedStatement psVentaId = conexion.prepareStatement(sqlObtenerVentaId);
            psVentaId.setString(1, numeroSerie);
            ResultSet rsVentaId = psVentaId.executeQuery();

            if (rsVentaId.next()) {
                int idVenta = rsVentaId.getInt("id_venta");

                // Consultar los detalles de la venta
                String sqlObtenerDetalles = "SELECT articulos_id, cantidad FROM detalle_ventas WHERE id_venta = ?";
                PreparedStatement psDetalles = conexion.prepareStatement(sqlObtenerDetalles);
                psDetalles.setInt(1, idVenta);
                ResultSet rsDetalles = psDetalles.executeQuery();

                // Actualizar el stock para cada producto
                String sqlActualizarStock = "UPDATE articulos SET stock = stock + ? WHERE id = ?";
                PreparedStatement psActualizarStock = conexion.prepareStatement(sqlActualizarStock);

                while (rsDetalles.next()) {
                    int articuloId = rsDetalles.getInt("articulos_id");
                    int cantidadDevuelta = rsDetalles.getInt("cantidad");

                    psActualizarStock.setInt(1, cantidadDevuelta);
                    psActualizarStock.setInt(2, articuloId);
                    psActualizarStock.executeUpdate();
                }

                // Eliminar los detalles de la venta
                String sqlEliminarDetalles = "DELETE FROM detalle_ventas WHERE id_venta = ?";
                PreparedStatement psEliminarDetalles = conexion.prepareStatement(sqlEliminarDetalles);
                psEliminarDetalles.setInt(1, idVenta);
                psEliminarDetalles.executeUpdate();

                // Eliminar la venta
                String sqlEliminarVenta = "DELETE FROM ventas WHERE id_venta = ?";
                PreparedStatement psEliminarVenta = conexion.prepareStatement(sqlEliminarVenta);
                psEliminarVenta.setInt(1, idVenta);
                psEliminarVenta.executeUpdate();

                JOptionPane.showMessageDialog(this, "Devolución total realizada y stock actualizado.");
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la venta con el número de serie proporcionado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al realizar la devolución: " + e.getMessage());
        }
    }


    private void realizarDevolucionProducto() {
        int filaSeleccionada = tablaProductosVenta.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para devolver.");
            return;
        }

        int idDetalle = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        int cantidadDevuelta = (int) modeloTabla.getValueAt(filaSeleccionada, 2); // Cantidad del producto
        String nombreProducto = (String) modeloTabla.getValueAt(filaSeleccionada, 1); // Nombre del producto (opcional para mensajes)

        try (Connection conexion = ConexionBD.conectar()) {
            // Obtener el ID del artículo relacionado al detalle de venta
            String sqlObtenerArticuloId = "SELECT articulos_id FROM detalle_ventas WHERE id_detalle = ?";
            PreparedStatement psArticulo = conexion.prepareStatement(sqlObtenerArticuloId);
            psArticulo.setInt(1, idDetalle);
            ResultSet rsArticulo = psArticulo.executeQuery();

            if (rsArticulo.next()) {
                int articuloId = rsArticulo.getInt("articulos_id");

                // Actualizar el stock del artículo
                String sqlActualizarStock = "UPDATE articulos SET stock = stock + ? WHERE id = ?";
                PreparedStatement psActualizarStock = conexion.prepareStatement(sqlActualizarStock);
                psActualizarStock.setInt(1, cantidadDevuelta);
                psActualizarStock.setInt(2, articuloId);
                psActualizarStock.executeUpdate();

                // Eliminar el detalle de la venta
                String sqlEliminarDetalle = "DELETE FROM detalle_ventas WHERE id_detalle = ?";
                PreparedStatement psEliminarDetalle = conexion.prepareStatement(sqlEliminarDetalle);
                psEliminarDetalle.setInt(1, idDetalle);
                psEliminarDetalle.executeUpdate();

                // Actualizar la tabla visual y mostrar mensaje
                modeloTabla.removeRow(filaSeleccionada);
                JOptionPane.showMessageDialog(this, "Producto \"" + nombreProducto + "\" devuelto y stock actualizado.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el artículo relacionado con este detalle.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al devolver el producto: " + e.getMessage());
        }
    }


    private void limpiarFormulario() {
        txtNumeroSerie.setText("");
        txtClienteDevolucion.setText("");
        modeloTabla.setRowCount(0);
    }
    
}
