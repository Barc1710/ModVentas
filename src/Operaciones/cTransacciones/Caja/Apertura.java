package Operaciones.cTransacciones.Caja;

import javax.swing.text.PlainDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import javax.swing.*;
import java.awt.*;

public class Apertura extends JFrame {
    private static double numero = 0; // Variable para almacenar el número ingresado

    public Apertura() {
        super("Apertura");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("Apertura de Caja", SwingConstants.CENTER);
        titulo.setFont(new Font("Verdana", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Panel central
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        JLabel lbl = new JLabel("Monto de Apertura:");
        JTextField txt = new JTextField(10);


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

        // Botón "Aceptar"
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> {
            try {
                numero = Double.parseDouble(txt.getText());
                JOptionPane.showMessageDialog(this, "Monto de apertura guardado: " + numero, "Información", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cierra la ventana actual

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lbl);
        panel.add(txt);
        panel.add(btnAceptar);
        add(panel, BorderLayout.CENTER);
    }

    public static double getNumero() {
        return numero;
    }
}
