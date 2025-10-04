package com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageGrantDTO {
    private Long id;
    private Long locationId;
    private Long externalId;
    private String identifier;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalDuration;
    private BigDecimal totalVolume;
    private BigDecimal maximumFlowRate;
    private List<UsageGrantMonthlyDTO> monthlyGrants;
}
