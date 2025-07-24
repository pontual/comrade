package com.pontual_telemetria.pontual_monitor_api.web.dto.control;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlDTO {
    private Long id;
    private Long locationId;
    private String deviceId;
    private String deviceStatus;
    private LocalDateTime dtDeviceActivate;
    private LocalDateTime dtDeviceDeactivation;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ControlReadingRequesterDTO> readings;
}
