package com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.dailyoperation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record DailyCalculatedItemDTO(
        LocalDate day,
        BigDecimal mvDailyHours,
        BigDecimal dailyHoursOverride,
        BigDecimal effDailyHours,
        BigDecimal instFlowRate,
        BigDecimal effVolume,
        String source,
        String overrideUpdatedBy,
        OffsetDateTime overrideUpdatedAt
) {
}
