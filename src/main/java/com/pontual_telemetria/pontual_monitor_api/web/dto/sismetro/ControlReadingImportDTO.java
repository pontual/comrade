package com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlReadingImportDTO {
    private Long id;
    private Double readingValue;
    private LocalDateTime dtReading;
    private LocalDateTime createdAt;
    private String createdBy;
    private String observation;
    private String status;
    private String tag;
    private String average;
    private BigDecimal calculatedVolume;
    private BigDecimal rawVolume;
}
