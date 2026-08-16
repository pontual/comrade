package com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {
    private Long id;
    private String brand;
    private String identifier;
    private LocalDateTime createdAt;
    private Boolean active;
    private String linkedPatrimony;
    private Boolean isFonteDadosApiAna;
}
