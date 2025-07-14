package com.pontual_telemetria.pontual_monitor_api.domain.model.customer;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.Constants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Constants.TABLE_LOCATION, schema = Constants.SCHEMA_CUSTOMER)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private Long externalId;

    private String description;

    @Column(name = "requester_id")
    private Integer requesterId;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "location")
    private String locationName;

    @Column(name = "category_id")
    private Integer categoryId;

    private String category;

    @Column(name = "type_tech_id")
    private Integer typeTechId;

    @Column(name = "type_tech")
    private String typeTech;

    private String observation;

    @Column(name = "brand_id")
    private Integer brandId;

    private String brand;

    @Column(name = "model_id")
    private Integer modelId;

    private String model;

    private String serial;

    private String patrimony;

    private String tag;

    @Column(name = "data_matrix")
    private String dataMatrix;

    private String details;

    private Integer status;

    @Column(name = "included_at")
    private LocalDateTime includedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "guarantee_until")
    private LocalDateTime guaranteeUntil;

    private String situation;

    @Column(name = "situation_id")
    private Integer situationId;
}