package com.pontual_telemetria.pontual_monitor_api.domain.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyAggRow {
    Long getExternalId();
    LocalDate getDay();
    BigDecimal getDailyPulseDiff();
    BigDecimal getDailyOpHours();
    BigDecimal getInstFlowRate();
    BigDecimal getCalculatedDailyMeasure();
}
