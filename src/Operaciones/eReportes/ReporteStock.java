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
import java.io.File;

public class ReporteStock {
    public void generarPDF() throws IOException {
        // Ruta base para guardar el archivo
        String basePath = "C:\\Users\\belth\\OneDrive\\Documentos\\Stock";
        String pdfPath = getDynamicFileName(basePath, "pdf");

        // Crear documento y página
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // Crear contentStream
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        try {
            System.out.println("Generando el PDF...");

            // Centrar el título
            String titulo = "Reporte de Stock - Grupo 4";
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

            String[] headers = {"ID", "Nombre", "Stock"};
            float[] colWidths = {50, 350, 100};

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
                    String query = "SELECT id, nombre, stock FROM articulos";
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
                                String.valueOf(rs.getInt("id")),
                                rs.getString("nombre"),
                                String.valueOf(rs.getInt("stock"))
                        };
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
                Desktop.getDesktop().open(file);
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
            contentStream.showText(rowData[i]);
            contentStream.endText();

            xPosition += cellWidth;
        }
    }

}
