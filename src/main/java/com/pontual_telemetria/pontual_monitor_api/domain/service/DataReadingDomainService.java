package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro.DataExtractionResult;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro.ControlReadingImportDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DataReadingDomainService {

    public DataExtractionResult excelToJsonParse(MultipartFile file) throws PontualMonitorException {

        if(file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")){
            throw new PontualMonitorException(
                    "Arquivo vázio ou tipo inválido",
                    "TYPE_FILE_ERROR",
                    HttpStatus.BAD_REQUEST,
                    "O arquivo está vazio ou possui formato incorreto. Tipo de arquivo esperado: .xlsx."
            );
        }

        List<ControlReadingImportDTO> resultList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is))
        {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if(rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row row = rows.next();

                Long id = getLongValue(row.getCell(0));
                Double readingValue = getDoubleValue(row.getCell(1));
                String dtReadingStr = getStringValue(row.getCell(2));
                String createdAtStr = getStringValue(row.getCell(3));
                String createdBy = getStringValue(row.getCell(4));
                String observation = getStringValue(row.getCell(5));
                String status = getStringValue(row.getCell(6));
                String tag = getStringValue(row.getCell(7));
                String average = getStringValue(row.getCell(8));

                ControlReadingImportDTO readObj = ControlReadingImportDTO.builder()
                        .id(id)
                        .readingValue(readingValue)
                        .dtReading(stringToDateTime(dtReadingStr))
                        .createdAt(stringToDateTime(createdAtStr))
                        .createdBy(createdBy)
                        .observation(observation)
                        .status(status)
                        .tag(tag)
                        .average(average)
                        .build();

                resultList.add(readObj);
            }

            String fileName = file.getOriginalFilename();
            int totalRows = sheet.getPhysicalNumberOfRows() - 1;
            int validRows = resultList.size();
            int invalidRows = totalRows - validRows;

            return DataExtractionResult.builder()
                    .fileName(fileName)
                    .totalRows(totalRows)
                    .validRows(validRows)
                    .invalidRows(invalidRows)
                    .resultList(resultList)
                    .build();

        } catch (Exception e){
            throw new PontualMonitorException(
                    "Erro ao extrair dados do XLS",
                    "EXTRACT_SERVICE_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocorreu um erro ao processar o arquivo Excel: " + e.getMessage() + "error: " + e
            );
        }
    }

    private static Long getLongValue(Cell cell){
        if(cell == null) return null;
        try {
            if(cell.getCellType() == CellType.NUMERIC){
                return (long) cell.getNumericCellValue();
            } else {
                return Long.valueOf(cell.toString().split("\\.")[0]);
            }
        } catch (Exception e){
            return null;
        }
    }

    private static String getStringValue(Cell cell) {
        return cell == null ? null : cell.toString().trim();
    }

    private static Double getDoubleValue(Cell cell) {
        if (cell == null) return null;
        try {
            if(cell.getCellType() == CellType.NUMERIC){
                return cell.getNumericCellValue();
            } else {
                return Double.parseDouble(cell.toString().replace(",", "."));
            }

        } catch (Exception e) {
            return null;
        }
    }

    private static LocalDateTime stringToDateTime(String dateTimeStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}
