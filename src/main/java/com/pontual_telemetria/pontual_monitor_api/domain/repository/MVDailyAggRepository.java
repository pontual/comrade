package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.view.MvDailyAgg;
import com.pontual_telemetria.pontual_monitor_api.domain.model.view.MvDailyAggId;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.projection.DailyAggRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface MVDailyAggRepository extends JpaRepository<MvDailyAgg, MvDailyAggId> {

    @Query(value = """
        SELECT DISTINCT EXTRACT(YEAR FROM day)::int AS y
          FROM sch_view.mv_daily_agg
         WHERE external_id = :ext
         ORDER BY y DESC
        """, nativeQuery = true)
    List<Integer> findAvailableYears(@Param("ext") Long externalId);

    @Query(value = """
        SELECT
          external_id                            AS externalId,
          day                                    AS day,
          daily_pulse_diff                       AS dailyPulseDiff,
          daily_op_hours                         AS dailyOpHours,
          inst_flow_rate                         AS instFlowRate,
          (daily_pulse_diff * inst_flow_rate)    AS calculatedDailyMeasure
        FROM sch_view.mv_daily_agg
        WHERE external_id = :ext
          AND EXTRACT(YEAR FROM day) = :year
        ORDER BY day
        """, nativeQuery = true)
    List<DailyAggRow> findDailyAggByYear(@Param("ext") Long externalId, @Param("year") int year);
}
