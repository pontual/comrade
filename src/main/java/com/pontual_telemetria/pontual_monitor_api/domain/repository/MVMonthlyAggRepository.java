package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.view.MvMonthlyAgg;
import com.pontual_telemetria.pontual_monitor_api.domain.model.view.MvMonthlyAggId;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.projection.MonthlyAggRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface MVMonthlyAggRepository extends JpaRepository<MvMonthlyAgg, MvMonthlyAggId> {

    @Query(value = """
        SELECT
          external_id       AS externalId,
          ym                AS ym,
          monthly_volume    AS monthlyVolume,
          monthly_op_hours  AS monthlyOpHours
        FROM sch_view.mv_monthly_agg
        WHERE external_id = :ext
          AND EXTRACT(YEAR FROM ym) = :year
        ORDER BY ym
        """, nativeQuery = true)
    List<MonthlyAggRow> findMonthlyAggByYear(@Param("ext") Long externalId, @Param("year") int year);
}
