package com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory;

import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.Constants;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Constants.TABLE_USAGE_GRANT, schema = Constants.SCHEMA_REGULATORY,
        indexes = {
                @Index(name = "idx_usage_grant_location_id", columnList = "location_id"),
                @Index(name = "idx_usage_grant_dates", columnList = "start_date, end_date")
        }
)
public class UsageGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "location_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_grant_location")
    )
    private Location location;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "total_duration", precision = 10, scale = 2)
    private BigDecimal totalDuration;

    @Column(name = "total_volume",  precision = 10, scale = 2)
    private BigDecimal totalVolume;

    @OneToMany(mappedBy = "usageGrant", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("year ASC, month ASC")
    private List<UsageGrantMonthly> monthlyGrants;
}

