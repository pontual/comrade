package com.pontual_telemetria.pontual_monitor_api.web.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Long id;
    private Long externalId;
    private String description;
    private Integer requesterId;
    private Integer locationId;
    private String locationName;
    private Integer categoryId;
    private String category;
    private Integer typeTechId;
    private String typeTech;
    private String observation;
    private Integer brandId;
    private String brand;
    private Integer modelId;
    private String model;
    private String serial;
    private String patrimony;
    private String tag;
    private String dataMatrix;
    private String details;
    private Integer status;
    private LocalDateTime includedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime guaranteeUntil;
    private String situation;
    private Integer situationId;
}
