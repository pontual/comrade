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
@Table(name = "mv_daily_agg", schema = "sch_view")
@Immutable
public class MvDailyAgg {

    @EmbeddedId
    private MvDailyAggId id;

    @Column(name = "daily_pulse_diff", nullable = false)
    private BigDecimal dailyPulseDiff;

    @Column(name = "daily_op_hours", nullable = false)
    private BigDecimal dailyOpHours;

    @Column(name = "inst_flow_rate", nullable = false)
    private BigDecimal instFlowRate;
}
