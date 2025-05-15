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

public class GestionCliente extends JFrame {
    private DefaultTableModel tableModel;
    private JTable tbl1;
    private JTextField dniField,nombresField, apellidosField, telefonoField, direccionField, txtBuscar;
    private int selectedId = -1; // ID de la fila seleccionada

    public GestionCliente() {
        setTitle("Gestión de Clientes");
        setSize(800, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        JLabel lbl1 = new JLabel("Administrar Clientes");
        lbl1.setFont(new Font("verdana", Font.BOLD, 24));
        panel1.add(lbl1);
        add(panel1, BorderLayout.NORTH);

        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.setBorder(BorderFactory.createTitledBorder("Clientes"));

        String[] columnNames = {"Id","DNI", "Nombres", "Apellidos", "Teléfono", "Dirección"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Todas las celdas no editables
            }
        };

        tbl1 = new JTable(tableModel);
        JScrollPane scroll1 = new JScrollPane(tbl1);
        panel2.add(scroll1, BorderLayout.CENTER);

        // Evento para seleccionar fila y llenar los JTextFields
        tbl1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tbl1.getSelectedRow();
                if (selectedRow != -1) {
                    selectedId = (int) tableModel.getValueAt(selectedRow, 0); // Obtener ID
                    dniField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    nombresField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    apellidosField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    telefonoField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    direccionField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel buscar = new JLabel("Buscar Cliente:");
        txtBuscar = new JTextField(15);
        JButton btnBuscar = new JButton("\uD83D\uDD0D");
        btnBuscar.addActionListener(e -> buscarCliente());
        panelBuscar.add(buscar);
        panelBuscar.add(txtBuscar);
        panelBuscar.add(btnBuscar);
        panel2.add(panelBuscar, BorderLayout.NORTH);
        add(panel2, BorderLayout.CENTER);

        JPanel panel4 = new JPanel(new GridLayout(13, 1, 5, 5));
        panel4.setBorder(BorderFactory.createTitledBorder("Datos Cliente"));

        JLabel lblDni = new JLabel("DNI:");
        dniField = new JTextField(15);

        JLabel lblNombres = new JLabel("Nombres:");
        nombresField = new JTextField(15);

        JLabel lblApellidos = new JLabel("Apellidos:");
        apellidosField = new JTextField(15);

        JLabel lblTelefono = new JLabel("Teléfono:");
        telefonoField = new JTextField(15);

        JLabel lblDireccion = new JLabel("Dirección:");
        direccionField = new JTextField(15);

        panel4.add(lblDni);
        panel4.add(dniField);
        panel4.add(lblNombres);
        panel4.add(nombresField);
        panel4.add(lblApellidos);
        panel4.add(apellidosField);
        panel4.add(lblTelefono);
        panel4.add(telefonoField);
        panel4.add(lblDireccion);
        panel4.add(direccionField);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton actualizar = new JButton("Actualizar \uD83D\uDD04\n");
        JButton eliminar = new JButton("Eliminar ❌");
        JButton nuevo = new JButton("Nuevo ✅");
        JButton limpiar = new JButton("Limpiar ⌫");

        actualizar.addActionListener(e -> actualizarCliente());
        eliminar.addActionListener(e -> eliminarCliente());
        nuevo.addActionListener(e -> agregarCliente());
        limpiar.addActionListener(e -> limpiarCampos());

        panelButtons.add(nuevo);
        panelButtons.add(actualizar);
        panelButtons.add(eliminar);
        panelButtons.add(limpiar);

        JPanel panelizquierda = new JPanel(new  BorderLayout());
        panelizquierda.add(panel4, BorderLayout.CENTER);

        add(panelizquierda, BorderLayout.WEST);

        add(panelButtons, BorderLayout.SOUTH);

        cargarDatos();
        setVisible(true);
    }

    private void cargarDatos() {
        Connection conexion = ConexionBD.conectar();
        if (conexion != null) {
            String query = "SELECT * FROM clientes";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String dni = rs.getString("DNI");
                    String nombres = rs.getString("nombres");
                    String apellidos = rs.getString("apellidos");
                    String telefono = rs.getString("telefono");
                    String direccion = rs.getString("direccion");
                    tableModel.addRow(new Object[]{id, dni, nombres, apellidos, telefono, direccion});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarCliente() {
        String buscarTexto = txtBuscar.getText().trim();
        if (buscarTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un término para buscar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nombres = tableModel.getValueAt(i, 1).toString().toLowerCase();
            if (nombres.contains(buscarTexto.toLowerCase())) {
                tbl1.setRowSelectionInterval(i, i);
                tbl1.scrollRectToVisible(tbl1.getCellRect(i, 0, true));
                selectedId = (int) tableModel.getValueAt(i, 0);
                nombresField.setText(tableModel.getValueAt(i, 1).toString());
                apellidosField.setText(tableModel.getValueAt(i, 2).toString());
                telefonoField.setText(tableModel.getValueAt(i, 3).toString());
                direccionField.setText(tableModel.getValueAt(i, 4).toString());
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarCliente() {
        String dni = dniField.getText().trim();
        String nombre = nombresField.getText().trim();
        String apellido = apellidosField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String direccion = direccionField.getText().trim();

        if (dni.isEmpty()||nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO clientes (dni, nombres, apellidos, telefono, direccion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, telefono);
            ps.setString(5, direccion);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente agregado correctamente.");
            cargarDatos();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCliente() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dni = dniField.getText().trim();
        String nombre = nombresField.getText().trim();
        String apellido = apellidosField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String direccion = direccionField.getText().trim();

        String query = "UPDATE clientes SET dni=?, nombres=?, apellidos=?, telefono=?, direccion=? WHERE id=?";
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, telefono);
            ps.setString(5, direccion);
            ps.setInt(6, selectedId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
            cargarDatos();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM clientes WHERE id=?";
            try (Connection conexion = ConexionBD.conectar();
                 PreparedStatement ps = conexion.prepareStatement(query)) {
                ps.setInt(1, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
                cargarDatos();
                limpiarCampos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        dniField.setText("");
        nombresField.setText("");
        apellidosField.setText("");
        telefonoField.setText("");
        direccionField.setText("");
        tbl1.clearSelection();
        selectedId = -1;
    }
}
