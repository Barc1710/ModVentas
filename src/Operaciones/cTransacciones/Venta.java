package Operaciones.cTransacciones;

import Conexion.ConexionBD;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Venta extends JFrame {
    private JTextField txtCliente, recibido, vuelto,
            txtproductoencontrado, txtclienteencontrado,
            txtProducto, txtCantidad, txtPrecioUnitario, txtTotalVenta, txtSerie;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnMenos, btnMas;
    private JComboBox<String> cmbDoc;
    JCheckBox checkBox;

    public Venta() {
        setTitle("Punto de Venta");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel de título
        JPanel panel = new JPanel();
        JLabel lbltitulo = new JLabel("VENTAS");
        lbltitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(lbltitulo);

        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));

        // Panel Datos Cliente
        JPanel panelDatosCliente = new JPanel();
        panelDatosCliente.setLayout(new GridLayout(2, 1));
        panelDatosCliente.setBorder(BorderFactory.createTitledBorder("Datos Cliente"));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtCliente = new JTextField(10);
        JButton btnBuscarCliente = new JButton("🔍");
        fila1.add(new JLabel("DNI Cliente:"));
        fila1.add(txtCliente);
        fila1.add(btnBuscarCliente);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        txtclienteencontrado = new JTextField(30);
        txtclienteencontrado.setEditable(false);
        fila2.add(txtclienteencontrado);

        panelDatosCliente.add(fila1);
        panelDatosCliente.add(fila2);

        // Panel Datos Producto
        JPanel panelDatosProducto = new JPanel();
        panelDatosProducto.setLayout(new GridLayout(4, 1));
        panelDatosProducto.setBorder(BorderFactory.createTitledBorder("Datos Producto"));

        JPanel filaProducto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtProducto = new JTextField(10);
        JButton btnBuscarProducto = new JButton("🔍");
        filaProducto.add(new JLabel("Producto:"));
        filaProducto.add(txtProducto);
        filaProducto.add(btnBuscarProducto);

        JPanel fila5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtproductoencontrado = new JTextField(30);
        txtproductoencontrado.setEditable(false);
        fila5.add(txtproductoencontrado);

        JPanel filaCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnMenos = new JButton("-");
        btnMenos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int cantidadActual = Integer.parseInt(txtCantidad.getText());
                    if (cantidadActual > 1) { // Evitar cantidades menores a 1
                        txtCantidad.setText(String.valueOf(cantidadActual - 1));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Venta.this, "Cantidad inválida.");
                }
            }
        });
        txtCantidad = new JTextField("1", 5);
        txtCantidad.setHorizontalAlignment(JTextField.CENTER);
       txtCantidad.setEditable(false);
        btnMas = new JButton("+");
        btnMas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int cantidadActual = Integer.parseInt(txtCantidad.getText());
                    txtCantidad.setText(String.valueOf(cantidadActual + 1));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Venta.this, "Cantidad inválida.");
                }
            }
        });
        txtPrecioUnitario = new JTextField(5);
        txtPrecioUnitario.setEditable(false);

        filaCantidad.add(new JLabel("Cantidad:"));
        filaCantidad.add(btnMenos);
        filaCantidad.add(txtCantidad);
        filaCantidad.add(btnMas);
        filaCantidad.add(new JLabel("Precio:"));
        filaCantidad.add(txtPrecioUnitario);

        JPanel filaAgregar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAgregarProducto = new JButton("Agregar Producto");
        filaAgregar.add(btnAgregarProducto);

        panelDatosProducto.add(filaProducto);
        panelDatosProducto.add(fila5);
        panelDatosProducto.add(filaCantidad);
        panelDatosProducto.add(filaAgregar);

        // Panel Datos Venta
        JPanel panelDatosVentas = new JPanel();
        panelDatosVentas.setLayout(new GridLayout(4,2));
        panelDatosVentas.setBorder(BorderFactory.createTitledBorder("Datos Venta"));

        JLabel lblDoc = new JLabel("Documento: ");
        cmbDoc = new JComboBox<>(new String[]{"Boleta", "Factura"});

        JLabel efectivoresivido = new JLabel("Efectivo Recibido: ");
        recibido = new JTextField(7);

        JLabel lblvuelto = new JLabel("Vuelto: ");
        vuelto = new JTextField(7);
        vuelto.setEditable(false);

        checkBox = new JCheckBox("Completo");

        JButton calvuelto = new JButton("Calcular Vuelto");

        panelDatosVentas.add(lblDoc);
        panelDatosVentas.add(cmbDoc);
        panelDatosVentas.add(efectivoresivido);
        panelDatosVentas.add(recibido);
        panelDatosVentas.add(lblvuelto);
        panelDatosVentas.add(vuelto);
        panelDatosVentas.add(checkBox);
        panelDatosVentas.add(calvuelto);

        panelIzquierdo.add(panelDatosCliente);
        panelIzquierdo.add(panelDatosProducto);
        panelIzquierdo.add(panelDatosVentas);

        add(panelIzquierdo, BorderLayout.WEST);

        // Panel central para tabla de productos
        JPanel central = new JPanel(new BorderLayout());
        central.setBorder(BorderFactory.createTitledBorder("Venta"));
        modeloTabla = new DefaultTableModel(new Object[]{"Nro.", "Producto", "Precio Unitario", "Cantidad", "Total"}, 0);
        tablaProductos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        JPanel serie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblSerie = new JLabel("Numero de Venta:");

        txtSerie = new JTextField(5); // Inicializar el campo
        txtSerie.setEditable(false);
        txtSerie.setText(String.valueOf(generarNumeroSerie())); // Asignar el ID generado
        serie.add(lblSerie);
        serie.add(txtSerie);
        central.add(serie, BorderLayout.NORTH);
        central.add(scrollPane, BorderLayout.CENTER);

        add(central, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtTotalVenta = new JTextField(10);
        txtTotalVenta.setEditable(false);
        JButton btneliminarfila = new JButton("Eliminar ");
        JButton btnGenerarVenta = new JButton("Generar Venta");
        JButton btnCancelarVenta = new JButton("Cancelar");

        panelInferior.add(btneliminarfila);
        panelInferior.add(new JLabel("Total a Pagar: S/"));

        panelInferior.add(txtTotalVenta);
        panelInferior.add(btnGenerarVenta);
        panelInferior.add(btnCancelarVenta);

        add(panel, BorderLayout.NORTH);
        add(panelInferior, BorderLayout.SOUTH);

        // Action listeners y demás configuraciones
        btneliminarfila.addActionListener(e -> eliminarProducto());
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        btnBuscarProducto.addActionListener(e -> buscarProducto());
        btnAgregarProducto.addActionListener(e -> agregarProducto());
        btnGenerarVenta.addActionListener(e -> generarVenta());
        btnCancelarVenta.addActionListener(e -> cancelarVenta());
        calvuelto.addActionListener(e -> calcularVuelto());

        setVisible(true);
    }


    private void calcularVuelto() {
        try {
            double totalVenta = Double.parseDouble(txtTotalVenta.getText());
            double efectivoRecibido = Double.parseDouble(recibido.getText());

            if (efectivoRecibido < totalVenta) {
                JOptionPane.showMessageDialog(this, "El efectivo recibido es menor que el total de la venta.");
                vuelto.setText("");
            } else {
                double cambio = efectivoRecibido - totalVenta;
                vuelto.setText(String.format("%.2f", cambio));
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos.");
        }
    }


    private void buscarCliente() {
        String dni = txtCliente.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un DNI.");
            return;
        }

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement("SELECT nombres, apellidos FROM clientes WHERE dni = ?")) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtclienteencontrado.setText(rs.getString("nombres") + " " + rs.getString("apellidos"));
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + e.getMessage());
        }
    }


    private void buscarProducto() {
        String articulo = txtProducto.getText().trim(); // Usa el texto actual como criterio de búsqueda
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement("SELECT id, nombre, precio FROM articulos WHERE nombre LIKE ?")) {
            stmt.setString(1, "%" + articulo + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtproductoencontrado.setText(rs.getString("nombre"));
                txtPrecioUnitario.setText(rs.getString("precio"));
            } else {
                JOptionPane.showMessageDialog(this, "Artículo no encontrado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar artículo: " + e.getMessage());
        }
    }

    private void agregarProducto() {
        try {
            if (txtproductoencontrado.getText().isEmpty() || txtPrecioUnitario.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete los datos del producto.");
                return;
            }

            String producto = txtproductoencontrado.getText();
            double precioUnitario = Double.parseDouble(txtPrecioUnitario.getText());
            int cantidad = Integer.parseInt(txtCantidad.getText());

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
                return;
            }

            // Verificar stock antes de agregar el producto
            int idArticulo = obtenerIdArticulo(producto);
            try (Connection conexion = ConexionBD.conectar()) {
                if (!verificarStockDisponible(conexion, idArticulo, cantidad)) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente para el producto: " + producto);
                    return;
                }
            }

            double total = precioUnitario * cantidad;
            modeloTabla.addRow(new Object[]{modeloTabla.getRowCount() + 1, producto, precioUnitario, cantidad, total});
            calcularTotalVenta();

            // Limpiar campos después de agregar el producto
            txtProducto.setText("");
            txtproductoencontrado.setText("");
            txtPrecioUnitario.setText("");
            txtCantidad.setText("1");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar el stock: " + e.getMessage());
        }
    }

    // Método para eliminar fila seleccionada
    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada != -1) {
            modeloTabla.removeRow(filaSeleccionada);
            recalcularNumerosFila();
            calcularTotalVenta();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar.");
        }
    }

    // Método para recalcular los números de fila después de eliminar una
    private void recalcularNumerosFila() {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            modeloTabla.setValueAt(i + 1, i, 0); // Actualizar el número de fila
        }
    }


    private void calcularTotalVenta() {
        double totalVenta = 0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            totalVenta += (double) modeloTabla.getValueAt(i, 4);
        }
        txtTotalVenta.setText(String.valueOf(totalVenta));
    }

    private void cancelarVenta() {
        modeloTabla.setRowCount(0);
        txtCliente.setText("");
        txtclienteencontrado.setText("");
        txtProducto.setText("");
        txtproductoencontrado.setText("");
        txtPrecioUnitario.setText("");
        txtCantidad.setText("1");
        txtTotalVenta.setText("");
        txtSerie.setText(String.valueOf(generarNumeroSerie()));
        limpiarCamposPago();
    }


    private int obtenerIdCliente(String cliente) throws SQLException {
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement("SELECT id FROM clientes WHERE CONCAT(nombres, ' ', apellidos) = ?")) {
            stmt.setString(1, cliente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
                throw new SQLException("Cliente no encontrado: " + cliente);
            }
        }
    }

    private int obtenerIdArticulo(String nombreArticulo) throws SQLException {
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement("SELECT id FROM articulos WHERE nombre = ?")) {
            stmt.setString(1, nombreArticulo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Artículo no encontrado: " + nombreArticulo);
            }
        }
    }
    private void generarVenta() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Debe agregar al menos un artículo.");
            return;
        }

        if (vuelto.getText().isEmpty() && !checkBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "Debe calcular el vuelto o marcar como venta completa.");
            return;
        }

        Connection conexion = null; // Declarar la conexión aquí
        try {
            conexion = ConexionBD.conectar();
            if (conexion == null) {
                throw new SQLException("Error al establecer la conexión con la base de datos.");
            }

            conexion.setAutoCommit(false); // Iniciar la transacción

            // Generar número de serie para la venta
            String numeroSerie = generarNumeroSerie();

            // Insertar la venta con el número de serie
            String sqlVenta = "INSERT INTO ventas (cliente_id, total, documento, numero_serie) OUTPUT INSERTED.id_venta VALUES (?, ?, ?, ?)";
            int idVenta;
            try (PreparedStatement stmtVenta = conexion.prepareStatement(sqlVenta)) {
                stmtVenta.setInt(1, obtenerIdCliente(txtclienteencontrado.getText()));
                stmtVenta.setDouble(2, Double.parseDouble(txtTotalVenta.getText()));
                stmtVenta.setString(3, cmbDoc.getSelectedItem().toString());
                stmtVenta.setString(4, numeroSerie); // Aquí asignas el número de serie

                ResultSet rs = stmtVenta.executeQuery(); // Ejecutar y capturar el resultado
                if (rs.next()) {
                    idVenta = rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la venta.");
                }
            }

            // Inserción de los detalles de la venta
            String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, articulos_id, cantidad, precio_unitario, total) VALUES (?, ?, ?, ?, ?)";
            String sqlActualizarStock = "UPDATE articulos SET stock = stock - ? WHERE id = ?";

            try (PreparedStatement stmtDetalle = conexion.prepareStatement(sqlDetalle);
                 PreparedStatement stmtActualizarStock = conexion.prepareStatement(sqlActualizarStock)) {

                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    String nombreArticulo = modeloTabla.getValueAt(i, 1).toString();
                    int idArticulo = obtenerIdArticulo(nombreArticulo);
                    double precioUnitario = Double.parseDouble(modeloTabla.getValueAt(i, 2).toString());
                    int cantidadVendida = Integer.parseInt(modeloTabla.getValueAt(i, 3).toString());
                    double total = Double.parseDouble(modeloTabla.getValueAt(i, 4).toString());

                    // Verificar que haya suficiente stock antes de continuar
                    if (!verificarStockDisponible(conexion, idArticulo, cantidadVendida)) {
                        throw new SQLException("Stock insuficiente para el artículo: " + nombreArticulo);
                    }

                    // Insertar el detalle de venta
                    stmtDetalle.setInt(1, idVenta); // ID de la venta
                    stmtDetalle.setInt(2, idArticulo); // ID del artículo
                    stmtDetalle.setInt(3, cantidadVendida); // Cantidad vendida
                    stmtDetalle.setDouble(4, precioUnitario); // Precio unitario
                    stmtDetalle.setDouble(5, total); // Total
                    stmtDetalle.addBatch(); // Usar batch para múltiples inserciones

                    // Actualizar el stock del artículo
                    stmtActualizarStock.setInt(1, cantidadVendida);
                    stmtActualizarStock.setInt(2, idArticulo);
                    stmtActualizarStock.addBatch();
                }

                // Ejecutar los batchs de inserción y actualización
                stmtDetalle.executeBatch();
                stmtActualizarStock.executeBatch();
            }

            conexion.commit(); // Confirmar la transacción
            JOptionPane.showMessageDialog(this, "Venta generada correctamente.");

            // Limpiar campos después de generar la venta
            cancelarVenta();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar la venta: " + e.getMessage());
            try {
                if (conexion != null) {
                    conexion.rollback(); // Si hay error, revertir los cambios
                }
            } catch (SQLException rollbackException) {
                JOptionPane.showMessageDialog(this, "Error al revertir la transacción: " + rollbackException.getMessage());
            }
        } finally {
            try {
                if (conexion != null) {
                    conexion.close(); // Asegurarse de cerrar la conexión
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    private boolean verificarStockDisponible(Connection conexion, int idArticulo, int cantidadSolicitada) throws SQLException {
        String sql = "SELECT stock FROM articulos WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idArticulo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int stockDisponible = rs.getInt("stock");
                return stockDisponible >= cantidadSolicitada;
            }
        }
        return false; // Si no se encuentra el artículo, retornar falso
    }


    private void limpiarCamposPago() {
        recibido.setText("");
        vuelto.setText("");
        checkBox.setSelected(false);
    }

    private String generarNumeroSerie() {
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement("SELECT MAX(numero_serie) FROM ventas")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String numeroSerie = rs.getString(1);
                // Si ya existe un número de serie, incrementar el número.
                if (numeroSerie != null) {
                    int serie = Integer.parseInt(numeroSerie.replaceAll("[^0-9]", "")); // Obtener solo el número
                    return "V-" + (serie + 1); // Ejemplo: incrementar el número, y agregar el prefijo 'V-'
                } else {
                    return "V-1"; // Si no existe ningún número, iniciar en "V-1"
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar número de serie: " + e.getMessage());
        }
        return "V-1"; // Valor por defecto si hay error
    }


}