package com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponseDTO {
    private String intervention;
    private LocalDateTime schedule;
    private BigDecimal flowRate;
    private BigDecimal adjustedFlowRate;
    private Integer duration;
    private BigDecimal adjustedDuration;
    private BigDecimal volume;
    private Integer transmissionCode;
    private String responsible;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal adjustedVolume;
}
