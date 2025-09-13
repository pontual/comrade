package com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OperationSummaryDTO(
        Long externalId,
        BigDecimal durationOperationHours,
        BigDecimal durationUsageGrantHours,
        BigDecimal volumeTotalOperation,
        BigDecimal volumeUsageGrant,
        BigDecimal averageFlow,
        LocalDate lastRead,
        BigDecimal utilization
) {
}
