package com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageGrantRequestDTO {
    private Long locationId;
    private String identifier;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalDuration;
    private BigDecimal totalVolume;
    private BigDecimal maximumFlowRate;
}
