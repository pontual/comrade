package com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyVolumeDTO {
    private String month;
    private BigDecimal monthlyMeasure;
    BigDecimal capturedAvgFlowRate;
    BigDecimal grantedAvgFlowRate;
}
