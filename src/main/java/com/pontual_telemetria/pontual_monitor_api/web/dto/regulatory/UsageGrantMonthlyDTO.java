package com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageGrantMonthlyDTO {
    private Long id;
    private Integer year;
    private Integer month;
    private BigDecimal flowRate;
    private BigDecimal hoursDay;
    private BigDecimal daysMonth;
    private BigDecimal maximumVolume;
}
