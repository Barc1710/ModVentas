package Operaciones.cTransacciones.Caja;

import Conexion.ConexionBD;

import javax.swing.text.PlainDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Cierre extends JFrame {
    public Cierre() {
        super("Cierre");
        setSize(400, 200);
        setVisible(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("Cierre de Caja", SwingConstants.CENTER);
        titulo.setFont(new Font("Verdana", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Panel central
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        JLabel lbl = new JLabel("Monto de Cierre:");
        JTextField txt = new JTextField(10);

        // Restringir entrada a solo números con punto decimal
        PlainDocument doc = (PlainDocument) txt.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]*\\.?[0-9]*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]*\\.?[0-9]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        // Botón "Cerrar"
        JButton btnCerrar = new JButton("Cierre");
        btnCerrar.addActionListener(e -> {
            try {
                double numeroCierre = Double.parseDouble(txt.getText());
                double totalVentas = sumarVentas();
                double numeroApertura = Apertura.getNumero();
                double totalEsperado = numeroApertura + totalVentas;

                if (numeroCierre == totalEsperado) {
                    JOptionPane.showMessageDialog(this, "¡Cierre exitoso! Total esperado: " + totalEsperado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Monto incorrecto.\nTotal esperado: " + totalEsperado + "\nMonto ingresado: " + numeroCierre, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lbl);
        panel.add(txt);
        panel.add(btnCerrar);
        add(panel, BorderLayout.CENTER);
    }

    private double sumarVentas() {
        double total = 0;
        String query = "SELECT SUM(dv.total) AS total_ventas FROM detalle_ventas dv";

        try (Connection conexion = ConexionBD.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                total = rs.getDouble("total_ventas");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al calcular el total de ventas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return total;
    }
}
