package Operaciones.dConsultas;

import Conexion.ConexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConsultaStock extends JFrame {
    private DefaultTableModel tableModel;
    private JTable tbl1;
    private JTextField txtBuscar;
    private JComboBox<String> comboCategorias;
    private int selectedId = -1;

    public ConsultaStock() {
        setTitle("Lista Artículos");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior con título y filtros
        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel filatitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl1 = new JLabel("Stock");
        lbl1.setFont(new Font("Vardana", Font.BOLD, 24));
        filatitulo.add(lbl1);

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel buscar = new JLabel("Buscar Producto:");
        txtBuscar = new JTextField(15);
        JButton btnBuscar = new JButton("\uD83D\uDD0D");

        comboCategorias = new JComboBox<>();
        comboCategorias.addItem("Todas"); // Opción para todas las categorías
        cargarCategorias(); // Llenar el combo con las categorías de la base de datos

        JButton btnFiltrar = new JButton("Filtrar");

        // Añadir acciones a los botones
        btnBuscar.addActionListener(e -> buscarProducto());
        btnFiltrar.addActionListener(e -> filtrarPorCategoria());

        panelBuscar.add(buscar);
        panelBuscar.add(txtBuscar);
        panelBuscar.add(btnBuscar);
        panelBuscar.add(comboCategorias);
        panelBuscar.add(btnFiltrar);

        panel1.add(filatitulo, BorderLayout.NORTH);
        panel1.add(panelBuscar, BorderLayout.CENTER);
        add(panel1, BorderLayout.NORTH);

        // Panel con la tabla de artículos
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "Nombre", "Cantidad"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tbl1 = new JTable(tableModel);
        JScrollPane scroll1 = new JScrollPane(tbl1);
        panel2.add(scroll1, BorderLayout.CENTER);

        add(panel2, BorderLayout.CENTER);

        // Cargar todos los datos inicialmente
        cargarDatos(null);
    }

    private void cargarDatos(String categoria) {
        Connection conexion = ConexionBD.conectar();
        if (conexion != null) {
            String query = "SELECT * FROM articulos";
            if (categoria != null && !categoria.equals("Todas")) {
                query += " WHERE categoria = '" + categoria + "'";
            }

            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                // Limpiar el modelo antes de cargar nuevos datos
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    int cantidad = rs.getInt("stock");
                    tableModel.addRow(new Object[]{id, nombre, cantidad});
                }
            } catch (SQLException e) {
                System.err.println("Error al cargar datos: " + e.getMessage());
            }
        }
    }

    private void cargarCategorias() {
        Connection conexion = ConexionBD.conectar();
        if (conexion != null) {
            String query = "SELECT DISTINCT categoria FROM articulos";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    comboCategorias.addItem(rs.getString("categoria"));
                }
            } catch (SQLException e) {
                System.err.println("Error al cargar categorías: " + e.getMessage());
            }
        }
    }

    private void buscarProducto() {
        String buscarTexto = txtBuscar.getText().trim();
        if (buscarTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un término para buscar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nombre = tableModel.getValueAt(i, 1).toString().toLowerCase();
            if (nombre.contains(buscarTexto.toLowerCase())) {
                tbl1.setRowSelectionInterval(i, i); // Seleccionar la fila encontrada
                tbl1.scrollRectToVisible(tbl1.getCellRect(i, 0, true)); // Asegurarse de que sea visible
                selectedId = (int) tableModel.getValueAt(i, 0); // Obtener ID de la fila seleccionada

                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void filtrarPorCategoria() {
        String categoriaSeleccionada = (String) comboCategorias.getSelectedItem();
        cargarDatos(categoriaSeleccionada);
    }
}
