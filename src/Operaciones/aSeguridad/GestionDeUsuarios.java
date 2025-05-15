package Operaciones.aSeguridad;

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

public class GestionDeUsuarios extends JFrame {
    private DefaultTableModel tableModel;
    private JTable tblUsuarios;
    private JTextField usuarioField, contrasenaField, nombreField, apellidosField;
    private JComboBox<String> rolCombo; // Uso exclusivo del JComboBox para el rol
    private int selectedId = -1; // ID del usuario seleccionado

    public GestionDeUsuarios() {
        setTitle("Gestión de Usuarios");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior
        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("Administrar Usuarios");
        lblTitulo.setFont(new Font("Verdana", Font.BOLD, 20));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central (tabla)
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "Usuario", "Contraseña", "Nombre", "Apellidos", "Rol"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Celdas no editables
            }
        };

        tblUsuarios = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(tblUsuarios);
        panelTabla.add(scroll, BorderLayout.CENTER);

        // Evento para seleccionar una fila y llenar los campos
        tblUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblUsuarios.getSelectedRow();
                if (selectedRow != -1) {
                    selectedId = (int) tableModel.getValueAt(selectedRow, 0);
                    usuarioField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    contrasenaField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    nombreField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    apellidosField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    rolCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
                }
            }
        });

        add(panelTabla, BorderLayout.CENTER);

        // Panel inferior (formulario y botones)
        JPanel panelFormulario = new JPanel(new GridLayout(8, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelFormulario.setBackground(new Color(245, 245, 245));

        JLabel lblUsuario = new JLabel("Usuario       :");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 15));
        usuarioField = new JTextField();

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Arial", Font.BOLD, 15));
        contrasenaField = new JTextField();

        JLabel lblNombre = new JLabel("Nombre       :");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
        nombreField = new JTextField();

        JLabel lblApellidos = new JLabel("Apellidos    :");
        lblApellidos.setFont(new Font("Arial", Font.BOLD, 15));
        apellidosField = new JTextField();

        JLabel lblRol = new JLabel("Rol               :");
        lblRol.setFont(new Font("Arial", Font.BOLD, 15));
        rolCombo = new JComboBox<>(new String[]{"Administrador", "Empleado"});

        panelFormulario.add(lblUsuario);
        panelFormulario.add(usuarioField);
        panelFormulario.add(lblContrasena);
        panelFormulario.add(contrasenaField);
        panelFormulario.add(lblNombre);
        panelFormulario.add(nombreField);
        panelFormulario.add(lblApellidos);
        panelFormulario.add(apellidosField);
        panelFormulario.add(lblRol);
        panelFormulario.add(rolCombo);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        // Configuración de botones
        configurarBoton(btnNuevo, new Color(175, 199, 175));
        configurarBoton(btnActualizar, new Color(154, 185, 211));
        configurarBoton(btnEliminar, new Color(211, 141, 141));
        configurarBoton(btnLimpiar, new Color(189, 168, 111));

        btnNuevo.addActionListener(e -> {
            String usuario = usuarioField.getText().trim();
            String contrasena = contrasenaField.getText().trim();
            String nombre = nombreField.getText().trim();
            String apellidos = apellidosField.getText().trim();
            String rol = rolCombo.getSelectedItem().toString();

            if (usuario.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            agregarUsuario(usuario, contrasena, nombre, apellidos, rol);
        });

        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnNuevo);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel izquierda = new JPanel(new BorderLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registro"));
        izquierda.add(panelFormulario, BorderLayout.CENTER);
        add(izquierda, BorderLayout.WEST);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        cargarDatos();
        setVisible(true);
    }

    private void cargarDatos() {
        Connection conexion = ConexionBD.conectar();
        if (conexion != null) {
            String query = "SELECT * FROM usuario";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String usuario = rs.getString("usuario");
                    String contrasena = rs.getString("contraseña");
                    String nombre = rs.getString("nombre");
                    String apellidos = rs.getString("apellido");
                    String rol = rs.getString("rol");
                    tableModel.addRow(new Object[]{id, usuario, contrasena, nombre, apellidos, rol});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void agregarUsuario(String usuario, String contrasena, String nombre, String apellidos, String rol) {
        String query = "INSERT INTO usuario (usuario, contraseña, nombre, apellido, rol) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ps.setString(3, nombre);
            ps.setString(4, apellidos);
            ps.setString(5, rol);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario registrado con éxito.");
            cargarDatos();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarUsuario() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String usuario = usuarioField.getText().trim();
        String contrasena = contrasenaField.getText().trim();
        String nombre = nombreField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String rol = rolCombo.getSelectedItem().toString();

        if (usuario.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "UPDATE usuario SET usuario = ?, contraseña = ?, nombre = ?, apellido = ?, rol = ? WHERE id = ?";
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ps.setString(3, nombre);
            ps.setString(4, apellidos);
            ps.setString(5, rol);
            ps.setInt(6, selectedId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario actualizado con éxito.");
            cargarDatos();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "DELETE FROM usuario WHERE id = ?";
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, selectedId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.");
            cargarDatos();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        usuarioField.setText("");
        contrasenaField.setText("");
        nombreField.setText("");
        apellidosField.setText("");
        rolCombo.setSelectedIndex(0);
        selectedId = -1;
    }

    private void configurarBoton(JButton boton, Color color) {
        boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

}
