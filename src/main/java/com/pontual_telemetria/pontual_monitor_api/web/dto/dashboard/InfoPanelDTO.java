package com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoPanelDTO {
    private Long locationId;
    private List<DailyVolumeDTO> dailyVolumeList;
    private List<MonthlyVolumeDTO> monthlyVolumeList;
    private List<UsageGrantDashboardInfoDTO> usageGrantDashboardInfo;
}
