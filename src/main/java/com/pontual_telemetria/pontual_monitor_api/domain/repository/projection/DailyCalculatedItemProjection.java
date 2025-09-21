package com.pontual_telemetria.pontual_monitor_api.domain.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public interface DailyCalculatedItemProjection {
    LocalDate getDay();
    BigDecimal getMvDailyHours();
    BigDecimal getDailyHoursOverride();
    String getOverrideUpdatedBy();
    OffsetDateTime getOverrideUpdatedAt();
}
