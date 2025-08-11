package com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.instantaneousflowtate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstantaneousFlowRateResponseDTO {
    private Long id;
    private Long locationId;
    private BigDecimal measurement;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
