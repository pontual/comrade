package com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelemetryDataParser {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static List<TelemetryResponseDTO> parse(String csvContent) throws Exception {
        List<TelemetryResponseDTO> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");

                TelemetryResponseDTO dto = new TelemetryResponseDTO(
                        row[0].trim(),                                      // intervention
                        LocalDateTime.parse(row[1].trim(), FORMATTER),      // schedule
                        new BigDecimal(row[2].trim()),                      // flowRate
                        null,                                               // adjustedFlowRate
                        Integer.parseInt(row[3].trim()),                    // duration
                        null,                                               // adjustedDuration
                        new BigDecimal(row[4].trim()),                      // volume
                        Integer.parseInt(row[5].trim()),                    // transmissionCode
                        row[6].trim(),                                      // responsible
                        LocalDateTime.parse(row[7].trim(), FORMATTER),      // startDate
                        LocalDateTime.parse(row[8].trim(), FORMATTER),      // endDate
                        null                                                // adjustedVolume
                );
                result.add(dto);
            }
        }

        return result;
    }
}
