package com.pontual_telemetria.pontual_monitor_api.domain.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MonthlyAggRow {
    Long getExternalId();
    LocalDate getYm();
    BigDecimal getMonthlyVolume();
    BigDecimal getMonthlyOpHours();

}