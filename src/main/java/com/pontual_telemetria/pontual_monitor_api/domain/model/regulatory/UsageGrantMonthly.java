package com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.Constants;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = Constants.TABLE_USAGE_GRANT_MONTHLY,
        schema = Constants.SCHEMA_REGULATORY,
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_usage_grant_monthly", columnNames = {"usage_grant_id", "month"})
        },
        indexes = {
                @Index(name = "idx_grant_monthly_grant_id", columnList = "usage_grant_id"),
                @Index(name = "idx_grant_monthly_month", columnList = "month")
        }
)
public class UsageGrantMonthly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usage_grant_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_usage_grant_monthly")
    )
    private UsageGrant usageGrant;

    @Column(nullable = false)
    private Integer month;

    @Column(name = "flow_rate", precision = 15, scale = 2)
    private BigDecimal flowRate;

    @Column(name = "hours_day", precision = 4, scale = 3)
    private BigDecimal hoursDay;

    @Column(name = "days_month", precision = 4, scale = 2)
    private BigDecimal daysMonth;

    @Column(name = "maximum_volume", precision = 15, scale = 2)
    private BigDecimal maximumVolume;
}
