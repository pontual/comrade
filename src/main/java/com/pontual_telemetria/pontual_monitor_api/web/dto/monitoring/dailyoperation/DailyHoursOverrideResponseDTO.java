package com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.dailyoperation;

import java.math.BigDecimal;

public record DailyHoursOverrideResponseDTO(
        Long id,
        Long externalId,
        String day,
        BigDecimal dailyHours,
        String updatedBy,
        String updatedAt
) {
}
