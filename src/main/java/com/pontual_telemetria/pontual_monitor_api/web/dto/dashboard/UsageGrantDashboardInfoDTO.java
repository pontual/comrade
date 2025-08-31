package com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageGrantDashboardInfoDTO {
    private Integer month;
    private BigDecimal monthlyUsageGrantVolume;
    private BigDecimal monthlyUsageGrantDuration;
    private BigDecimal monthlyOperationHours;
    private BigDecimal maxMonthlyOperationHours;
    private BigDecimal totalVolume;
    private BigDecimal totalDuration;
    private BigDecimal averageFlow;
}
