package com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OperationSummaryDTO {
    private Long externalId;
    private BigDecimal durationOperationHours;
    private BigDecimal durationUsageGrantHours;
    private BigDecimal volumeTotalOperation;
    private BigDecimal volumeUsageGrant;
    private BigDecimal averageFlow;
    private LocalDate lastRead;
    private BigDecimal maximumFlowRate;
    private BigDecimal utilization;
}
