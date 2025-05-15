package Operaciones.eReportes;

import Conexion.ConexionBD;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.Desktop;

public class ReporteVentas {
    public void generarPDF() throws IOException {
        // Ruta base para guardar el archivo
        String basePath = "C:\\Users\\belth\\OneDrive\\Documentos\\Ventas";
        String pdfPath = getDynamicFileName(basePath, "pdf");

        // Crear documento y página
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        try {
            System.out.println("Generando el PDF...");

            // Centrar el título
            String titulo = "Reporte de Ventas - Grupo 4";
            float tituloWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(titulo) / 1000 * 18;
            float tituloX = (page.getMediaBox().getWidth() - tituloWidth) / 2;

            // Escribir el título
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(tituloX, 750);
            contentStream.showText(titulo);
            contentStream.endText();

            // Encabezado de la tabla
            float margin = 50;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float rowHeight = 20;
            float cellMargin = 5;
            float yPosition = 700; // Posición inicial Y

            String[] headers = {"ID Venta", "N° Serie", "Nombre Artículo", "Cliente", "Cantidad", "Total", "Fecha de Venta"};


            float[] colWidths = {60, 60, 100, 100, 60, 60, 100}; // Ajustar aquí los tamaños de las columnas

            // Dibujar encabezado de la tabla
            drawRow(contentStream, yPosition, margin, tableWidth, rowHeight, headers, colWidths, true);
            yPosition -= rowHeight;

            // Conectar a la base de datos y llenar los datos
            Connection conexion = null;
            Statement stmt = null;
            ResultSet rs = null;

            try {
                conexion = ConexionBD.conectar();
                if (conexion != null) {
                    String query = "SELECT v.id_venta, v.numero_serie, a.nombre AS articulo, " +
                            "c.nombres + ' ' + c.apellidos AS cliente, dv.cantidad, dv.total, " +
                            "v.fecha " +
                            "FROM detalle_ventas dv " +
                            "JOIN ventas v ON dv.id_venta = v.id_venta " +
                            "JOIN articulos a ON dv.articulos_id = a.id " +
                            "JOIN clientes c ON v.cliente_id = c.id";

                    stmt = conexion.createStatement();
                    rs = stmt.executeQuery(query);

                    // Dibujar filas de datos
                    while (rs.next()) {
                        if (yPosition < 50) {
                            contentStream.close();
                            page = new PDPage();
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);
                            yPosition = 700;

                            // Dibujar encabezado en nueva página
                            drawRow(contentStream, yPosition, margin, tableWidth, rowHeight, headers, colWidths, true);
                            yPosition -= rowHeight;
                        }

                        String[] row = {
                                String.valueOf(rs.getInt("id_venta")),
                                rs.getString("numero_serie"),
                                rs.getString("articulo"),
                                rs.getString("cliente"),
                                String.valueOf(rs.getInt("cantidad")),
                                String.valueOf(rs.getDouble("total")),
                                rs.getDate("fecha").toString() // Usar formato adecuado para fecha
                        };

                        // Dibujar fila de datos
                        drawRow(contentStream, yPosition, margin, tableWidth, rowHeight, row, colWidths, false);
                        yPosition -= rowHeight;
                    }
                } else {
                    throw new IOException("Error al conectar a la base de datos.");
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener datos de la base: " + e.getMessage());
            }
        } finally {
            contentStream.close();
            document.save(pdfPath);
            document.close();
        }

        System.out.println("PDF generado correctamente: " + pdfPath);
        try {
            File file = new File(pdfPath);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file); // Abrir el archivo PDF generado
            } else {
                System.err.println("La funcionalidad de abrir archivos no está soportada en este sistema.");
            }
        } catch (IOException e) {
            System.err.println("Error al intentar abrir el archivo: " + e.getMessage());
        }
    }

    // Método para obtener un nombre de archivo dinámico
    private String getDynamicFileName(String basePath, String extension) {
        int counter = 1;
        String filePath = basePath + counter + "." + extension;

        while (new File(filePath).exists()) {
            counter++;
            filePath = basePath + counter + "." + extension;
        }

        return filePath;
    }

    // Método para dibujar filas en la tabla
    private void drawRow(PDPageContentStream contentStream, float yPosition, float margin, float tableWidth,
                         float rowHeight, String[] rowData, float[] colWidths, boolean isHeader) throws IOException {
        float xPosition = margin;

        // Dibujar celdas
        for (int i = 0; i < rowData.length; i++) {
            float cellWidth = colWidths[i];
            contentStream.addRect(xPosition, yPosition - rowHeight, cellWidth, rowHeight);
            contentStream.stroke();

            // Escribir texto dentro de la celda
            contentStream.beginText();
            contentStream.setFont(isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(xPosition + 5, yPosition - rowHeight + 5);

            // Asegurarse de que el texto no sea null
            String texto = rowData[i] != null ? rowData[i] : ""; // Asignar "" si es null
            contentStream.showText(texto);
            contentStream.endText();

            xPosition += cellWidth;
        }
    }
}
