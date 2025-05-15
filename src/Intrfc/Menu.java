package Intrfc;

import Operaciones.aSeguridad.GestionDeUsuarios;
import Operaciones.aSeguridad.RestablecerContraseña;
import Operaciones.aSeguridad.SesionUsuario;
import Operaciones.bMaestros.GestioArticulos;
import Operaciones.bMaestros.GestionCliente;
import Operaciones.bMaestros.ListaPrecios;
import Operaciones.cTransacciones.Caja.Apertura;
import Operaciones.cTransacciones.Caja.Cierre;
import Operaciones.cTransacciones.Devoluciones;
import Operaciones.dConsultas.ConsultaStock;
import Operaciones.dConsultas.ConsultaVentas;
import Operaciones.eReportes.ReporteStock;
import Operaciones.cTransacciones.Venta;
import Operaciones.eReportes.ReporteVentas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame  {
    public Menu(){
        setSize(800, 600);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p1=new JPanel();
        p1.setLayout(new FlowLayout());
        JLabel bnv=new JLabel("¡Bienvenido al Modulo de Ventas!");
        bnv.setFont(new Font("Display",Font.BOLD,24));
        p1.add(bnv);

        JPanel p2 =new JPanel();
        JMenuBar mb= new JMenuBar();
        mb.setBackground(new Color(42, 194, 208, 81));

        JMenu mSeguridad=new JMenu("Seguridad");
        mSeguridad.setFont(new Font("Arial",Font.BOLD,15));
        JMenu mConfi=new JMenu("Configuración");
        mConfi.setFont(new Font("Arial",Font.BOLD,15));
        JMenu mTransacciones=new JMenu("Transacciones");
        mTransacciones.setFont(new Font("Arial",Font.BOLD,15));
        JMenu mConsult=new JMenu("Consultas");
        mConsult.setFont(new Font("Arial",Font.BOLD,15));
        JMenu mReport=new JMenu("Reportes");
        mReport.setFont(new Font("Arial",Font.BOLD,15));
        JMenu msalir=new JMenu("Salir");
        msalir.setFont(new Font("Arial",Font.BOLD,15));

        p2.add(mb);
        mb.add(mSeguridad); mb.add(mConfi);
        mb.add(mTransacciones); mb.add(mConsult); mb.add(mReport);
        mb.add(msalir);

        JMenuItem msUser=new JMenuItem("Usuarios");
        msUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                SesionUsuario sc= new SesionUsuario();
                String rolUsuario = sc.getRol();
                if ("Administrador".equals(rolUsuario)) {
                    GestionDeUsuarios reg = new GestionDeUsuarios();

                } else {

                    JOptionPane.showMessageDialog(null, "No tiene permisos para esta acción.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        JMenuItem msRestablecer = new JMenuItem("Restablecer Contraseña");
        msRestablecer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RestablecerContraseña re=new RestablecerContraseña();
                re.setVisible(true);

            }
        });

        JMenuItem mcArt=new JMenuItem("Articulos");

        mcArt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                GestioArticulos gestioArticulos=new GestioArticulos();
            }
        });

        JMenuItem mcClientes=new JMenuItem("Clientes");

        mcClientes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                GestionCliente gc=new GestionCliente();
            }
        });

        JMenuItem mcLstPrecios=new JMenuItem("Lista de Precios");
        mcLstPrecios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ListaPrecios lp=new ListaPrecios();
                lp.setVisible(true);
            }
        });

        JMenuItem mtVenta=new JMenuItem("Ventas");
        JMenuItem mtDevol=new JMenuItem("Devoluciones");

        mtVenta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Venta pv= new Venta();
            }
        });

        mtDevol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Devoluciones dv=new Devoluciones();
            }
        });

        JMenu mtCaja=new JMenu("Caja");
        JMenuItem mtcApertura=new JMenuItem("Apertura");
        mtcApertura.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Apertura ap=new Apertura();
            }
        });
        JMenuItem mtcCierre=new JMenuItem("Cierre");

        mtcCierre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {Cierre ci=new Cierre();
            }
        });

        JMenuItem mcoStock=new JMenuItem("Consulta Stock");
        mcoStock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConsultaStock cs=new ConsultaStock();
                cs.setVisible(true);
            }

        });

        JMenuItem mcoVentas=new JMenuItem("Consulta Ventas");
        mcoVentas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConsultaVentas cv=new ConsultaVentas();
                cv.setVisible(true);
            }
        });

        JMenuItem mrStock=new JMenuItem("Reporte Stock");
        mrStock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ReporteStock reporte = new ReporteStock();
                    reporte.generarPDF();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        JMenuItem mrVentas=new JMenuItem("Reporte Ventas");
        mrVentas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ReporteVentas reporte = new ReporteVentas();
                    reporte.generarPDF();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem msCerrarSesion=new JMenuItem("Cerrar Sesion");
        JMenuItem msSalir=new JMenuItem("Salir");

        mSeguridad.add(msUser); mSeguridad.add(msRestablecer);
        mConfi.add(mcArt);
        mConfi.add(mcClientes);
        mConfi.add(mcLstPrecios);
        mTransacciones.add(mtVenta); mTransacciones.add(mtDevol);
        mTransacciones.add(mtCaja); mtCaja.add(mtcApertura); mtCaja.add(mtcCierre);
        mConsult.add(mcoStock); mConsult.add(mcoVentas);
        mReport.add(mrStock); mReport.add(mrVentas);
        msalir.add(msCerrarSesion); msalir.add(msSalir);


        msCerrarSesion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int salir = JOptionPane.showConfirmDialog(null,
                        "¿Estás seguro de que quieres cerrar sesión?",
                        "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (salir == JOptionPane.YES_OPTION) {

                    dispose();

                    IniciarLogin in = new IniciarLogin();
                    in.setVisible(true);
                }
            }
        });

        
        msSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null,
                        "¿Estás seguro de Salir?",
                        "Salir",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                dispose();
            }
        });

        setLayout(new BorderLayout());
        add(p1, BorderLayout.CENTER);
        add(p2, BorderLayout.NORTH);

        setVisible(true);
    }

}
