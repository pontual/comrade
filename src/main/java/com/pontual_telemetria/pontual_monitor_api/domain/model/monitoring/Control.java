package com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring;

import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.Constants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = Constants.TABLE_CONTROL,
        schema = Constants.SCHEMA_MONITORING,
        indexes = {
                @Index(name = "idx_control_location_id", columnList = "location_id"),
                @Index(name = "idx_control_external_id", columnList = "external_id"),
                @Index(name = "idx_control_device_id", columnList = "device_id"),
        }
)
public class Control {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "location_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_control_location")
    )
    private Location location;

    @Column(name = "external_id", nullable = false)
    private Long externalId;

    @Column(name = "device_id", length = 100, nullable = false)
    private String deviceId;

    @Column(name = "dt_device_activate", nullable = false)
    private LocalDateTime dtDeviceActivate;

    @Column(name = "dt_device_deactivation")
    private LocalDateTime dtDeviceDeactivation;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "control", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ControlReading> readings;
}
