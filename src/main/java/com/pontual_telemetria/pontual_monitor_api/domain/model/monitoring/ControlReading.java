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
        name = Constants.TABLE_CONTROL_READING,
        schema = Constants.SCHEMA_MONITORING,
        indexes = {
                @Index(name = "idx_control_reading_control_id", columnList = "control_id"),
                @Index(name = "idx_control_reading_dt_reading", columnList = "dt_reading"),
                @Index(name = "idx_control_reading_tag", columnList = "tag")
        }
)
public class ControlReading {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(
                name = "control_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_control_reading_control")
        )
        private Control control;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(
                name = "location_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_control_reading_location")
        )
        private Location location;

        @Column(name = "reading_value", precision = 10, scale = 3)
        private BigDecimal readingValue;

        @Column(name = "dt_reading")
        private LocalDateTime dtReading;

        @Column(name = "created_by", length = 255)
        private String createdBy;

        @Column(name = "observation", length = 255)
        private String observation;

        @Column(name = "status", length = 100)
        private String status;

        @Column(name = "tag", length = 100)
        private String tag;

        @Column(name = "average", length = 10)
        private String average;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;
}
