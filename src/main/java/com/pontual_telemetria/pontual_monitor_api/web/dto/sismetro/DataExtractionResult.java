package com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataExtractionResult {
    private String fileName;
    private int totalRows;
    private int validRows;
    private int invalidRows;
    List<ControlReadingImportDTO> resultList;
}
