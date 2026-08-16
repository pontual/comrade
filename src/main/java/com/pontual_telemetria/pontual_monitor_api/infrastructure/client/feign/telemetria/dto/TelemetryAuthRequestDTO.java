package com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.telemetria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryAuthRequestDTO {
    private String username;
    private String password;
}
