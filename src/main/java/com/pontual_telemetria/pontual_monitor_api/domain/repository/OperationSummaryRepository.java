package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.projection.OperationSummaryProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OperationSummaryRepository extends Repository<UsageGrant, Long> {

    @Query(value = """
    WITH params AS (
      SELECT
        :year AS y,
        make_date(:year, 1, 1)::date  AS start_date,
        make_date(:year,12,31)::date  AS end_date
    ),
    -- external_ids desse location com outorga válida em algum dia do ano
    exts AS (
      SELECT DISTINCT ug.external_id
      FROM sch_regulatory.usage_grant ug, params p
      WHERE ug.location_id   = :locationId
        AND ug.start_date::date <= p.end_date
        AND ug.end_date::date   >= p.start_date
    ),
    -- leituras da MV no ano
    base AS (
      SELECT
        m.external_id              AS external_id,
        m.day                      AS day,
        m.daily_op_hours           AS captured_hours,
        m.inst_flow_rate           AS day_flow_m3_h,
        m.calculated_daily_measure AS captured_volume_m3
      FROM sch_view.mv_daily_agg m
      JOIN params p ON TRUE
      JOIN exts e   ON e.external_id = m.external_id
      WHERE m.day BETWEEN p.start_date AND p.end_date
    ),
    -- outorga diária (quando houver sobreposição, pega a mais recente)
    grant_by_day AS (
      SELECT DISTINCT ON (ug.external_id, d::date)
        ug.external_id,
        d::date AS day,
        ugm.hours_day AS grant_hours_day,
        (
          COALESCE(
            ugm.maximum_volume,
            (COALESCE(ugm.flow_rate,0) * COALESCE(ugm.hours_day,0)
             * COALESCE(ugm.days_month,
                EXTRACT(DAY FROM (date_trunc('month', d) + INTERVAL '1 month - 1 day'))))
          )
          / NULLIF(
              COALESCE(ugm.days_month,
                EXTRACT(DAY FROM (date_trunc('month', d) + INTERVAL '1 month - 1 day'))),
              0
            )
        )::numeric AS grant_volume_m3_day
      FROM sch_regulatory.usage_grant ug
      JOIN sch_regulatory.usage_grant_monthly ugm
        ON ugm.usage_grant_id = ug.id
      JOIN params p ON TRUE
      CROSS JOIN generate_series(
        date_trunc('month', make_date(p.y, ugm.month, 1)),
        date_trunc('month', make_date(p.y, ugm.month, 1)) + INTERVAL '1 month - 1 day',
        INTERVAL '1 day'
      ) AS d
      WHERE ug.location_id = :locationId
        AND ug.start_date::date <= d::date
        AND ug.end_date::date   >= d::date
      ORDER BY ug.external_id, d::date, ug.start_date DESC
    ),
    -- agregado anual
    agg AS (
      SELECT
        b.external_id,
        SUM(b.captured_volume_m3)   AS volume_total_operation,
        SUM(b.captured_hours)       AS duration_operation_hours,

        -- vazão média captada = volume / duração
        CASE WHEN SUM(b.captured_hours) > 0
             THEN SUM(b.captured_volume_m3) / NULLIF(SUM(b.captured_hours),0)
             ELSE NULL
        END                         AS average_flow,

        COALESCE(SUM(g.grant_volume_m3_day), 0) AS volume_usage_grant,
        COALESCE(SUM(g.grant_hours_day),  0)    AS duration_usage_grant_hours,

        -- vazão média outorgada = volume / duração
        CASE WHEN COALESCE(SUM(g.grant_hours_day),0) > 0
             THEN COALESCE(SUM(g.grant_volume_m3_day),0) / NULLIF(SUM(g.grant_hours_day),0)
        END                         AS grant_average_flow,

        MAX(b.day)                  AS last_read
      FROM base b
      LEFT JOIN grant_by_day g
        ON g.external_id = b.external_id AND g.day = b.day
      GROUP BY b.external_id
    )
    SELECT
      a.external_id,
      a.duration_operation_hours,
      a.duration_usage_grant_hours,
      a.volume_total_operation,
      a.volume_usage_grant,
      a.average_flow,
      a.last_read,

      -- utilization = comprometimento da vazão (captada / outorgada * 100)
      CASE WHEN a.grant_average_flow > 0
        THEN ROUND(100 * a.average_flow / a.grant_average_flow, 2)
      END AS utilization
    FROM agg a
    ORDER BY a.external_id
    """, nativeQuery = true)
    List<OperationSummaryProjection> summaryByLocationId(
            @Param("locationId") Long locationId,
            @Param("year") Integer year
    );
}
