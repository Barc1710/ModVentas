package Operaciones.bMaestros;

import Conexion.ConexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GestioArticulos extends JFrame {
    private DefaultTableModel tableModel;
    private JTable tbl1;
    private JTextField nombreField, stockField, precioField, txtBuscar;
    private int selectedId = -1; // ID de la fila seleccionada
    private JComboBox<String> categoriaCombo;

    public GestioArticulos() {
        setTitle("Gestión de Artículos");
        setSize(800, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        JLabel lbl1 = new JLabel("Administrar Productos");
        lbl1.setFont(new Font("Verdana", Font.BOLD, 20));
        panel1.add(lbl1);
        add(panel1, BorderLayout.NORTH);

        JPanel central = new JPanel(new BorderLayout());
        central.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "Nombre", "Stock", "Precio", "Categoría"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Todas las celdas no editables
            }
        };

        tbl1 = new JTable(tableModel);
        JScrollPane scroll1 = new JScrollPane(tbl1);
        central.add(scroll1, BorderLayout.CENTER);

        // Evento para seleccionar fila y llenar los JTextFields y JComboBox
        tbl1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tbl1.getSelectedRow();
                if (selectedRow != -1) {
                    selectedId = (int) tableModel.getValueAt(selectedRow, 0); // Obtener ID
                    nombreField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    stockField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    precioField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    String categoria = tableModel.getValueAt(selectedRow, 4).toString();
                    categoriaCombo.setSelectedItem(categoria); // Actualizar el JComboBox
                }
            }
        });

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel buscar = new JLabel("Buscar Producto:");
        txtBuscar = new JTextField(15);
        JButton btnBuscar = new JButton("\uD83D\uDD0D");
        btnBuscar.addActionListener(e -> buscarProducto());
        panelBuscar.add(buscar);
        panelBuscar.add(txtBuscar);
        panelBuscar.add(btnBuscar);

        central.setBorder(BorderFactory.createTitledBorder("Productos"));
        central.add(panelBuscar, BorderLayout.NORTH);
        add(central, BorderLayout.CENTER);

        JPanel formularioarticulos = new JPanel(new GridLayout(13, 1, 2, 2));
        formularioarticulos.setBorder(BorderFactory.createTitledBorder("Registrar Producto"));

        JLabel lblNombre = new JLabel("Nombre:");
        nombreField = new JTextField(15);

        JLabel lblStock = new JLabel("Stock:");
        stockField = new JTextField(15);

        JLabel lblPrecio = new JLabel("Precio:");
        precioField = new JTextField(15);

        JLabel lblCategoria = new JLabel("Categoría:");
        categoriaCombo = new JComboBox<>(new String[]{"Frutas", "Ropa", "Tecnologia", "Muebles"});

        formularioarticulos.add(lblNombre);
        formularioarticulos.add(nombreField);
        formularioarticulos.add(lblStock);
        formularioarticulos.add(stockField);
        formularioarticulos.add(lblPrecio);
        formularioarticulos.add(precioField);
        formularioarticulos.add(lblCategoria);
        formularioarticulos.add(categoriaCombo);

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.add(formularioarticulos, BorderLayout.CENTER);
        add(izquierda, BorderLayout.WEST);

        JPanel panelbotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton actualizar = new JButton("Actualizar \uD83D\uDD04\n");
        JButton eliminar = new JButton("Eliminar ❌");
        JButton nuevo = new JButton("Nuevo ✅");
        JButton limpiar = new JButton("Limpiar ⌫");

        actualizar.addActionListener(e -> actualizarArticulo());
        eliminar.addActionListener(e -> eliminarArticulo());

        nuevo.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            String precio = precioField.getText().trim();
            String stock = stockField.getText().trim();
            String categoriaTexto = categoriaCombo.getSelectedItem().toString();

            if (nombre.isEmpty() || precio.isEmpty() || stock.isEmpty() || categoriaTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double precioDouble = Double.parseDouble(precio);
                int stockInt = Integer.parseInt(stock);

                agregarArticulo(nombre, precioDouble, stockInt, categoriaTexto);
                limpiarCampos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser numérico y el stock un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        limpiar.addActionListener(e -> limpiarCampos());

        panelbotones.add(nuevo);
        panelbotones.add(actualizar);
        panelbotones.add(eliminar);
        panelbotones.add(limpiar);

        add(panelbotones, BorderLayout.SOUTH);

        cargarDatos();
        setVisible(true);
    }

    private void cargarDatos() {
        Connection conexion = ConexionBD.conectar();
        if (conexion != null) {
            String query = "SELECT * FROM articulos";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    int stock = rs.getInt("stock");
                    double precio = rs.getDouble("precio");
                    String categoria = rs.getString("categoria");
                    tableModel.addRow(new Object[]{id, nombre, stock, precio, categoria});
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
                stockField.setText(tableModel.getValueAt(i, 2).toString());
                precioField.setText(tableModel.getValueAt(i, 3).toString());
                categoriaCombo.setSelectedItem(tableModel.getValueAt(i, 4).toString());
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarArticulo() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un artículo para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = nombreField.getText().trim();
        String precioTexto = precioField.getText().trim();
        String stockTexto = stockField.getText().trim();
        String categoria = categoriaCombo.getSelectedItem().toString();

        if (nombre.isEmpty() || precioTexto.isEmpty() || stockTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double precio = Double.parseDouble(precioTexto);
            int stock = Integer.parseInt(stockTexto);

            Connection conexion = ConexionBD.conectar();
            if (conexion != null) {
                String query = "UPDATE articulos SET nombre=?, stock=?, precio=?, categoria=? WHERE id=?";
                try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
                    pstmt.setString(1, nombre);
                    pstmt.setInt(2, stock);
                    pstmt.setDouble(3, precio);
                    pstmt.setString(4, categoria);
                    pstmt.setInt(5, selectedId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Artículo actualizado con éxito.");
                    cargarDatos();
                    limpiarCampos();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número y el stock un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el artículo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarArticulo() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un artículo para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este artículo?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conexion = ConexionBD.conectar();
            if (conexion != null) {
                String query = "DELETE FROM articulos WHERE id=?";
                try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
                    pstmt.setInt(1, selectedId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Artículo eliminado con éxito.");
                    cargarDatos();
                    limpiarCampos();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el artículo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void agregarArticulo(String nombre, double precio, int stock, String categoria) {
        Connection conexion = ConexionBD.conectar();
        if (conexion != null) {
            String query = "INSERT INTO articulos (nombre, stock, precio, categoria) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
                pstmt.setString(1, nombre);
                pstmt.setInt(2, stock);
                pstmt.setDouble(3, precio);
                pstmt.setString(4, categoria);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Artículo agregado con éxito.");
                cargarDatos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar el artículo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        selectedId = -1;
        nombreField.setText("");
        stockField.setText("");
        precioField.setText("");
        categoriaCombo.setSelectedIndex(0);
    }

}
