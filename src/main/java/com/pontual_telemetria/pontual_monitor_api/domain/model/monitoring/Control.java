package com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring;

import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.Constants;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
                @Index(name = "idx_control_device_id", columnList = "device_id"),
                @Index(name = "idx_control_dt_reading", columnList = "dt_reading"),
                @Index(name = "idx_control_tag", columnList = "tag")
        }
)
public class Control {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "location_id",
            foreignKey = @ForeignKey(name = "fk_control_location")
    )
    private Location location;

    @Column(name = "device_id", length = 12, nullable = false)
    private String deviceId;

    @Column(name = "dt_device_activate", nullable = false)
    private LocalDateTime dtDeviceActivate;

    @Column(name = "device_status", length = 30)
    private String deviceStatus;

    @Column(name = "reading_value", precision = 10, scale = 3)
    private BigDecimal readingValue;

    @Column(name = "dt_reading")
    private LocalDateTime dtReading;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "observation")
    private String observation;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "tag", length = 100)
    private String tag;

    @Column(name = "average", length = 10)
    private String average;

}
