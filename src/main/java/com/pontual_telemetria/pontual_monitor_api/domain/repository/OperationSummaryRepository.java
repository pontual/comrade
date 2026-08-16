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
        make_date(:year,12,31)::date  AS end_date,
        (make_date(:year,12,31) + INTERVAL '1 day')::date AS end_date_plus1
    ),
    
    exts AS (
      SELECT DISTINCT ug.external_id
      FROM sch_regulatory.usage_grant ug, params p
      WHERE ug.location_id = :locationId
        AND ug.start_date::date < p.end_date_plus1
        AND (ug.end_date IS NULL OR ug.end_date::date > p.start_date)
    ),
    
    base AS (
      SELECT
        v.external_id                    AS external_id,
        v.day                            AS day,
    
        CASE WHEN :capTo24
             THEN LEAST(v.eff_daily_hours, 24)
             ELSE v.eff_daily_hours
        END                              AS captured_hours,
    
        v.inst_flow_rate                 AS day_flow_m3_h,
    
        CASE WHEN :capTo24
             THEN (LEAST(v.eff_daily_hours, 24) * v.inst_flow_rate)
             ELSE v.eff_volume
        END                              AS captured_volume_m3
    
      FROM sch_monitoring.vw_daily_calculated_final v
      JOIN params p ON TRUE
      JOIN exts   e ON e.external_id = v.external_id
      WHERE v.day BETWEEN p.start_date AND p.end_date
    ),
    
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
        AND (ug.end_date IS NULL OR ug.end_date::date > d::date)
      ORDER BY ug.external_id, d::date, ug.start_date DESC
    ),
    
    latest_grant AS (
      SELECT DISTINCT ON (ug.external_id)
        ug.external_id,
        ug.maximum_flow_rate AS maximum_flow_rate
      FROM sch_regulatory.usage_grant ug, params p
      WHERE ug.location_id = :locationId
        AND ug.start_date::date < p.end_date_plus1
        AND (ug.end_date IS NULL OR ug.end_date::date > p.start_date)
      ORDER BY ug.external_id, ug.start_date DESC
    ),

    latest_control AS (
      SELECT DISTINCT ON (c.external_id)
        c.external_id,
        c.is_fonte_dados_api_ana
      FROM sch_monitoring.control c
      WHERE c.location_id = :locationId
      ORDER BY c.external_id, c.created_at DESC
    ),

    latest_reading_status AS (
      SELECT DISTINCT ON (cr.external_id)
        cr.external_id,
        cr.status
      FROM sch_monitoring.control_reading cr
      WHERE cr.location_id = :locationId
      ORDER BY cr.external_id, cr.dt_reading DESC
    ),
    
    agg AS (
      SELECT
        b.external_id,
        SUM(b.captured_volume_m3)   AS volume_total_operation,
        SUM(b.captured_hours)       AS duration_operation_hours,
        CASE WHEN SUM(b.captured_hours) > 0
             THEN SUM(b.captured_volume_m3) / NULLIF(SUM(b.captured_hours),0)
             ELSE NULL
        END                         AS average_flow,
        COALESCE(SUM(g.grant_volume_m3_day), 0) AS volume_usage_grant,
        COALESCE(SUM(g.grant_hours_day),  0)    AS duration_usage_grant_hours,
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
      lg.maximum_flow_rate AS maximum_flow_rate,
      CASE WHEN a.grant_average_flow > 0
        THEN ROUND(100 * a.average_flow / a.grant_average_flow, 2)
      END AS utilization,
      lc.is_fonte_dados_api_ana,
      lrs.status
    FROM agg a
    LEFT JOIN latest_grant lg  ON lg.external_id  = a.external_id
    LEFT JOIN latest_control lc ON lc.external_id = a.external_id
    LEFT JOIN latest_reading_status lrs ON lrs.external_id = a.external_id
    ORDER BY a.external_id
    """, nativeQuery = true)
    List<OperationSummaryProjection> summaryByLocationId(
            @Param("locationId") Long locationId,
            @Param("year") Integer year,
            @Param("capTo24") boolean capTo24
    );
}
