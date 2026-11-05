package com.example.service;

import com.example.model.Reserva;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExcelReportService {

    public ByteArrayInputStream generarReporteReservasExcel(List<Reserva> reservas) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte de Reservas");
            Row headerRow = sheet.createRow(0);

            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Usuario ID");
            headerRow.createCell(2).setCellValue("Espacio ID");
            headerRow.createCell(3).setCellValue("Fecha Inicio");
            headerRow.createCell(4).setCellValue("Fecha Fin");
            headerRow.createCell(5).setCellValue("Total Pagar ($)");

            int rowNum = 1;
            for (Reserva r : reservas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getId() != null ? r.getId() : 0);
                row.createCell(1).setCellValue(r.getUsuarioId() != null ? r.getUsuarioId() : 0);
                row.createCell(2).setCellValue(r.getEspacioId() != null ? r.getEspacioId() : 0);
                row.createCell(3).setCellValue(r.getFechaInicio() != null ? r.getFechaInicio().toString() : "");
                row.createCell(4).setCellValue(r.getFechaFin() != null ? r.getFechaFin().toString() : "");
                row.createCell(5).setCellValue(r.getTotalPagar() != null ? r.getTotalPagar() : 0.0);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}