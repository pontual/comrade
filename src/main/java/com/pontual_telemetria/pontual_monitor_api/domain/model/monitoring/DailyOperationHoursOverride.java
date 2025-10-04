package com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.Constants;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(
        name = Constants.TABLE_DAILY_OPERATION_HOURS_OVERRIDE,
        schema = Constants.SCHEMA_MONITORING
)
public class DailyOperationHoursOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="external_id", nullable=false)
    private Long externalId;

    @Column(name="day", nullable=false)
    private LocalDate day;

    @Column(name="daily_hours_override", nullable=false, precision=10, scale=3)
    private BigDecimal dailyHoursOverride;

    @Column(name="updated_by", nullable=false, length=100)
    private String updatedBy;

    @Column(
            name="updated_at",
            nullable=false,
            columnDefinition = "timestamptz default now()",
            insertable = false,
            updatable = false
    )
    private OffsetDateTime updatedAt;
}