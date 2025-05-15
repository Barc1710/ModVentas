package Operaciones.bMaestros;

import Conexion.ConexionBD;
import java.sql.PreparedStatement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ListaPrecios extends JFrame {
    private DefaultTableModel tableModel;
    private JTable tbl1;
    private JTextField txtBuscar, nombreField, precioField;
    private int selectedId = -1;

    public ListaPrecios() {
        setTitle("Lista Articulos");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        JLabel lbl1 = new JLabel("Lista de Precios");
        lbl1.setFont(new Font("verdana", Font.BOLD, 24));
        panel1.add(lbl1);

        add(panel1, BorderLayout.NORTH);

        JPanel panelcentral = new JPanel(new BorderLayout());
        panelcentral.setBorder(BorderFactory.createTitledBorder("Productos"));
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtBuscar = new JTextField(15);
        JButton btnBuscar = new JButton("\uD83D\uDD0D");
        btnBuscar.addActionListener(e -> buscarProducto());
        panelBuscar.add(txtBuscar);
        panelBuscar.add(btnBuscar);


        JPanel panel2 = new JPanel(new BorderLayout());
        String[] columnNames = {"ID", "Nombre", "Precio"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tbl1 = new JTable(tableModel);
        JScrollPane scroll1 = new JScrollPane(tbl1);
        panel2.add(scroll1, BorderLayout.CENTER);

        panelcentral.add(panelBuscar, BorderLayout.NORTH);
        panelcentral.add(panel2, BorderLayout.CENTER);
        add(panelcentral, BorderLayout.CENTER);

        cargarDatos();
    }


    private void cargarDatos() {
        Connection conexion = ConexionBD.conectar();
        if (conexion != null) {
            String query = "SELECT * FROM articulos";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                // Limpiar el modelo antes de cargar nuevos datos
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    double precio = rs.getDouble("precio");
                    tableModel.addRow(new Object[]{id, nombre, precio});
                }
            } catch (SQLException e) {
                System.err.println("Error al cargar datos: " + e.getMessage());
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
                tbl1.setRowSelectionInterval(i, i);
                tbl1.scrollRectToVisible(tbl1.getCellRect(i, 0, true));
                selectedId = (int) tableModel.getValueAt(i, 0);
                nombreField.setText(tableModel.getValueAt(i, 1).toString());

                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
