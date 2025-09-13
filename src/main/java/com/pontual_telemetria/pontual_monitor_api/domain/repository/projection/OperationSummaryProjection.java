package com.pontual_telemetria.pontual_monitor_api.domain.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface OperationSummaryProjection {
    Long getExternal_id();
    BigDecimal getDuration_operation_hours();
    BigDecimal getDuration_usage_grant_hours();
    BigDecimal getVolume_total_operation();
    BigDecimal getVolume_usage_grant();
    BigDecimal getAverage_flow();
    LocalDate getLast_read();
    BigDecimal getUtilization();
}
