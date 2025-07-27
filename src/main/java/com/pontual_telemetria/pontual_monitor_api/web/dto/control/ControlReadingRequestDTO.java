package com.pontual_telemetria.pontual_monitor_api.web.dto.control;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlReadingRequestDTO {
    private Long controlId;
    private Integer locationId;
    private BigDecimal readingValue;
    private LocalDateTime dtReading;
    private String createdBy;
    private String observation;
    private String status;
    private String tag;
    private String average;
}
