CREATE OR REPLACE VIEW sch_monitoring.vw_daily_calculated_final AS
WITH base AS (
    SELECT
        cr.external_id,
        cr.dt_reading::date AS day,
        cr.reading_value,
        cr.dt_reading,
        cr.calculated_volume,
        cr.created_by
    FROM sch_monitoring.control_reading cr
),
     per_day AS (
         SELECT
             b.external_id,
             b.day,
             (ARRAY_AGG(b.reading_value ORDER BY b.dt_reading ASC))[1]   AS first_value,
             (ARRAY_AGG(b.reading_value ORDER BY b.dt_reading DESC))[1]  AS last_value,
             SUM(b.calculated_volume)                                     AS total_calculated_volume,
             BOOL_OR(b.created_by = 'API Telemetria ANA')                AS is_api_source
         FROM base b
         GROUP BY b.external_id, b.day
     ),
     hours_horimeter AS (
         SELECT
             p.external_id,
             p.day,
             ROUND(GREATEST(p.last_value - p.first_value, 0)::numeric, 2) AS mv_daily_hours,
             p.total_calculated_volume,
             p.is_api_source
         FROM per_day p
     )
SELECT
    h.external_id,
    h.day,
    h.mv_daily_hours,
    o.daily_hours_override,
    COALESCE(o.daily_hours_override, h.mv_daily_hours)                AS eff_daily_hours,
    COALESCE(mv.inst_flow_rate, 0)::numeric(12,3)                     AS inst_flow_rate,
    CASE
        WHEN h.is_api_source THEN COALESCE(h.total_calculated_volume, 0)
        ELSE (COALESCE(o.daily_hours_override, h.mv_daily_hours) * COALESCE(mv.inst_flow_rate, 0))
    END::numeric(14,3)                                                AS eff_volume,
    CASE
        WHEN h.is_api_source THEN 'API'
        WHEN o.external_id IS NULL THEN 'MV'
        ELSE 'OVERRIDE'
        END                                                           AS source,
    o.updated_by                                                      AS override_updated_by,
    o.updated_at                                                      AS override_updated_at
FROM hours_horimeter h
         LEFT JOIN sch_monitoring.daily_operation_hours_override o
                   ON o.external_id = h.external_id AND o.day = h.day
         LEFT JOIN sch_view.mv_daily_agg mv
                   ON mv.external_id = h.external_id AND mv.day = h.day;