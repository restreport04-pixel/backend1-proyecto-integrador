package com.example.service;

import com.example.model.Reserva;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfReportService {

    public ByteArrayInputStream generarReporteReservasPdf(List<Reserva> reservas) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Reporte General de Reservas - Coworking")
                    .setBold()
                    .setFontSize(16));
            document.add(new Paragraph(" "));

            Table table = new Table(new float[]{1, 2, 2, 3, 3, 2});
            table.addHeaderCell("ID");
            table.addHeaderCell("Usuario ID");
            table.addHeaderCell("Espacio ID");
            table.addHeaderCell("Fecha Inicio");
            table.addHeaderCell("Fecha Fin");
            table.addHeaderCell("Total ($)");

            for (Reserva r : reservas) {
                table.addCell(r.getId() != null ? r.getId().toString() : "");
                table.addCell(r.getUsuarioId() != null ? r.getUsuarioId().toString() : "");
                table.addCell(r.getEspacioId() != null ? r.getEspacioId().toString() : "");
                table.addCell(r.getFechaInicio() != null ? r.getFechaInicio().toString() : "");
                table.addCell(r.getFechaFin() != null ? r.getFechaFin().toString() : "");
                table.addCell(r.getTotalPagar() != null ? r.getTotalPagar().toString() : "");
            }

            document.add(table);
            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}