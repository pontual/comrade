package com.pontual_telemetria.pontual_monitor_api.domain.model.view;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mv_monthly_agg", schema = "sch_view")
@Immutable
public class MvMonthlyAgg {

    @EmbeddedId
    private MvMonthlyAggId id;

    @Column(name = "monthly_volume", nullable = false)
    private BigDecimal monthlyVolume;

    @Column(name = "monthly_op_hours", nullable = false)
    private BigDecimal monthlyOpHours;
}
