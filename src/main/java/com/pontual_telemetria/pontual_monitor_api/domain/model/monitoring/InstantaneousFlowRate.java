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
        name = Constants.TABLE_INSTANTANEOUS_FLOW_RATE,
        schema = Constants.SCHEMA_MONITORING,
        indexes = {
                @Index(name = "idx_flow_location_id", columnList = "location_id"),
                @Index(name = "idx_flow_external_id", columnList = "external_id"),
                @Index(name = "idx_flow_start_end_date", columnList = "start_date, end_date")
        }
)
public class InstantaneousFlowRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "location_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_flow_location")
    )
    private Location location;

    @Column(name = "external_id", nullable = false)
    private Long externalId;

    @Column(precision = 10, scale = 2)
    private BigDecimal measurement;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;
}
