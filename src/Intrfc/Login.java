package Intrfc;

import Operaciones.aSeguridad.SesionUsuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import Conexion.ConexionBD;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JPanel{
    String usuario;
    String contrasena;

    public Login(){

        JPanel p1=new JPanel();
        JLabel in=new JLabel("Iniciar Sesión");
        in.setFont(new Font("Verdana", Font.BOLD, 20));
        p1.add(in);

        JPanel p2=new JPanel();
        p2.setLayout(new GridLayout(4,1));
        p2.setBorder(BorderFactory.createEmptyBorder(1,40,1,40));
        JLabel nm=new JLabel("Usuario");
        p2.add(nm);
        JTextField us=new JTextField();
        p2.add(us);
        JLabel cn=new JLabel("Contraseña");
        p2.add(cn);
        JPasswordField pw=new JPasswordField();
        p2.add(pw);


        JPanel p3=new JPanel();
        p3.setLayout(new GridLayout(3,1,10,10));
        p3.setBorder(BorderFactory.createEmptyBorder(5,90,5,90));

        JButton btnIniciar=new JButton("Iniciar");
        btnIniciar.setFont(new Font( "Arial", Font.BOLD, 14));
        btnIniciar.setBackground(new Color(13, 164, 178));
        btnIniciar.setForeground(Color.WHITE);

        p3.add(btnIniciar);

       btnIniciar.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                usuario = us.getText();
                contrasena = new String(pw.getPassword());

                Connection conexion = ConexionBD.conectar();
                if (conexion != null) {
                    try {
                        String sql = "SELECT * FROM usuario WHERE usuario = ? AND contraseña = ?";
                        PreparedStatement stmt = conexion.prepareStatement(sql);
                        stmt.setString(1, usuario);
                        stmt.setString(2, contrasena);

                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            String rol = rs.getString("rol");

                            SesionUsuario sc=new SesionUsuario();
                            SesionUsuario.iniciarSesion(usuario, rol);

                            Menu menu = new Menu();
                            menu.setVisible(true);

                            Window parent = SwingUtilities.getWindowAncestor(Login.this);
                            if (parent != null) {
                                parent.dispose();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                        }


                    } catch (SQLException ex) {

                        JOptionPane.showMessageDialog(null, "Error al verificar las credenciales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {

                    JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos. Intenta de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        setLayout(new BorderLayout(10,10));
        add(p1, BorderLayout.NORTH);
        add(p2, BorderLayout.CENTER);
        add(p3, BorderLayout.SOUTH);
        


    }




}
