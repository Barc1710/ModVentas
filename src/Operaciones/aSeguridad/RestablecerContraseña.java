package Operaciones.aSeguridad;

import Conexion.ConexionBD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RestablecerContraseña extends javax.swing.JFrame {
    String usuario;
    String contrasena;

    public RestablecerContraseña() {
        super("Restablecer Contraseña");
        setSize(400,400);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel p1 = new JPanel();
        JLabel in = new JLabel("Restablecer Contraseña");
        in.setFont(new Font("Verdana", Font.BOLD, 20));
        p1.add(in);

        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(6, 1));
        p2.setBorder(BorderFactory.createEmptyBorder(1, 40, 1, 40));
        JLabel nm = new JLabel("Usuario");
        p2.add(nm);
        JTextField us = new JTextField();
        p2.add(us);
        JLabel cn = new JLabel("Contraseña Actual");
        p2.add(cn);
        JTextField pw = new JTextField();
        p2.add(pw);
        JLabel cnn = new JLabel("Contraseña Nueva");
        p2.add(cnn);
        JTextField pwn = new JTextField();
        p2.add(pwn);

        JPanel p3 = new JPanel();
        p3.setLayout(new GridLayout(3, 1, 10, 10));
        p3.setBorder(BorderFactory.createEmptyBorder(5, 90, 5, 90));

        JButton bini = new JButton("Guardar");
        bini.setFont(new Font("Arial", Font.BOLD, 14));
        bini.setBackground(new Color(13, 164, 178));
        bini.setForeground(Color.WHITE);

        p3.add(bini);

        bini.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                usuario = us.getText();
                contrasena = pw.getText();
                String nuevaContrasena = pwn.getText();

                Connection conexion = ConexionBD.conectar();
                if (conexion != null) {
                    try {
                        // Verificar si el usuario y la contraseña actual son correctos
                        String sql = "SELECT * FROM usuario WHERE usuario = ? AND contraseña = ?";
                        PreparedStatement stmt = conexion.prepareStatement(sql);
                        stmt.setString(1, usuario);
                        stmt.setString(2, contrasena);

                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            // Actualizar la contraseña con la nueva
                            String updateSql = "UPDATE usuario SET contraseña = ? WHERE usuario = ?";
                            PreparedStatement updateStmt = conexion.prepareStatement(updateSql);
                            updateStmt.setString(1, nuevaContrasena);
                            updateStmt.setString(2, usuario);

                            int rowsAffected = updateStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "Contraseña actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                dispose();
                                Window parent = SwingUtilities.getWindowAncestor(RestablecerContraseña.this);
                                if (parent != null) {
                                    parent.dispose();
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Error al actualizar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Usuario o contraseña actual incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error al verificar las credenciales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos. Intenta de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setLayout(new BorderLayout(10, 10));
        add(p1, BorderLayout.NORTH);
        add(p2, BorderLayout.CENTER);
        add(p3, BorderLayout.SOUTH);
    }
}
