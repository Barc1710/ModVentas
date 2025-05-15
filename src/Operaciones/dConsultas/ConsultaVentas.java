package Operaciones.dConsultas;

import Conexion.ConexionBD;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ConsultaVentas extends JFrame {
    private JTable tablaVentas;
    private JScrollPane scrollPane;
    private JTextField txtBuscarSerie;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JComboBox<String> comboCategorias;

    public ConsultaVentas() {
        setTitle("Ventas Realizadas");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Crear modelo de tabla
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Venta");
        modelo.addColumn("Número de Serie");
        modelo.addColumn("Nombre Artículo");
        modelo.addColumn("Cliente");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Total");
        modelo.addColumn("Fecha de Venta");

        tablaVentas = new JTable(modelo);
        scrollPane = new JScrollPane(tablaVentas);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lbltitulo = new JLabel("Consulta de Ventas");
        lbltitulo.setFont(new Font("Verdana", Font.BOLD, 20));
        panel.add(lbltitulo);

        JPanel panelfiltros = new JPanel();
        txtBuscarSerie = new JTextField(10);
        txtFechaInicio = new JTextField(10);
        txtFechaFin = new JTextField(10);
        comboCategorias = new JComboBox<>();
        comboCategorias.addItem("Todas");
        comboCategorias.addItem("Tegnología");
        comboCategorias.addItem("Ropa");
        comboCategorias.addItem("Frutas");
        comboCategorias.addItem("Muebles");

        JButton btnBuscar = new JButton("Buscar");
        panelfiltros.add(new JLabel("N° Serie:"));
        panelfiltros.add(txtBuscarSerie);
        panelfiltros.add(new JLabel("Fecha Inicio:"));
        panelfiltros.add(txtFechaInicio);
        panelfiltros.add(new JLabel("Fecha Fin:"));
        panelfiltros.add(txtFechaFin);
        panelfiltros.add(new JLabel("Categoría:"));
        panelfiltros.add(comboCategorias);
        panelfiltros.add(btnBuscar);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.add(panelfiltros, BorderLayout.NORTH);
        contenido.add(scrollPane, BorderLayout.CENTER);

        add(panel, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);

        // Cargar ventas iniciales
        cargarVentas(modelo, null, null, null, null);

        // Acción del botón buscar
        btnBuscar.addActionListener(e -> {
            String numeroSerie = txtBuscarSerie.getText().trim();
            String fechaInicio = txtFechaInicio.getText().trim();
            String fechaFin = txtFechaFin.getText().trim();
            String categoria = comboCategorias.getSelectedItem().toString();
            if ("Todas".equals(categoria)) categoria = null;
            cargarVentas(modelo, numeroSerie, fechaInicio, fechaFin, categoria);
        });
    }

    private void cargarVentas(DefaultTableModel modelo, String numeroSerie, String fechaInicio, String fechaFin, String categoria) {
        modelo.setRowCount(0);

        String query = "SELECT v.id_venta, v.numero_serie, a.nombre AS articulo, " +
                "c.nombres + ' ' + c.apellidos AS cliente, dv.cantidad, dv.total, " +
                "v.fecha " +
                "FROM detalle_ventas dv " +
                "JOIN ventas v ON dv.id_venta = v.id_venta " +
                "JOIN articulos a ON dv.articulos_id = a.id " +
                "JOIN clientes c ON v.cliente_id = c.id " +
                "WHERE 1=1";

        if (numeroSerie != null && !numeroSerie.isEmpty()) {
            query += " AND v.numero_serie LIKE '%" + numeroSerie + "%'";
        }
        if (fechaInicio != null && !fechaInicio.isEmpty()) {
            query += " AND v.fecha >= '" + fechaInicio + "'";
        }
        if (fechaFin != null && !fechaFin.isEmpty()) {
            query += " AND v.fecha <='" + fechaFin + "'";
        }
        if (categoria != null && !categoria.isEmpty()) {
            query += " AND a.categoria = '" + categoria + "'";
        }

        try (Connection conexion = ConexionBD.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id_venta"));           // ID de la venta
                row.add(rs.getString("numero_serie"));    // Número de serie
                row.add(rs.getString("articulo"));        // Nombre del artículo
                row.add(rs.getString("cliente"));         // Cliente
                row.add(rs.getInt("cantidad"));           // Cantidad
                row.add(rs.getDouble("total"));           // Total
                row.add(formatoFecha.format(rs.getDate("fecha"))); // Fecha formateada
                modelo.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
