package com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryRequestDTO {
    private String deviceIdentifier;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
}
