package com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyVolumeDTO {
    private String day;
    private BigDecimal dailyMeasure;
    private BigDecimal dailyOperationHours;
    private BigDecimal maxDailyOperationHours;
    private BigDecimal monthInstantaneousFlowRate;
    private BigDecimal calculatedDailyMeasure;
}
