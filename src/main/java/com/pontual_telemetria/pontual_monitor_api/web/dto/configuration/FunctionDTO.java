package com.pontual_telemetria.pontual_monitor_api.web.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean enabled = false;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdate;
}
