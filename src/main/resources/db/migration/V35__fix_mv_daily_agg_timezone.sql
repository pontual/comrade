-- dt_reading is stored as local time (America/Sao_Paulo).
-- The previous AT TIME ZONE conversion treated local timestamps as UTC and shifted
-- them to BRT, causing readings at 21:00+ BRT to be attributed to the following day.
-- Using ::date directly preserves the correct local calendar date.

DROP MATERIALIZED VIEW IF EXISTS sch_view.mv_daily_agg CASCADE;

CREATE MATERIALIZED VIEW sch_view.mv_daily_agg AS
WITH base AS (
    SELECT
        cr.external_id,
        cr.dt_reading::date AS day,
        cr.reading_value,
        cr.dt_reading
    FROM sch_monitoring.control_reading cr
),
per_day AS (
    SELECT
        b.external_id,
        b.day,
        MAX(b.reading_value) AS last_value,
        MIN(b.reading_value) AS first_value,
        MIN(b.dt_reading)    AS first_ts,
        MAX(b.dt_reading)    AS last_ts
    FROM base b
    GROUP BY b.external_id, b.day
),
with_lag AS (
    SELECT
        p.*,
        LAG(p.last_value) OVER (PARTITION BY p.external_id ORDER BY p.day) AS prev_last
    FROM per_day p
),
with_flow AS (
    SELECT
        w.*,
        COALESCE(
            (
                SELECT ifr.measurement
                FROM sch_monitoring.instantaneous_flow_rate ifr
                WHERE ifr.external_id = w.external_id
                  AND date_trunc('month', w.day) >= date_trunc('month', ifr.start_date)
                  AND (ifr.end_date IS NULL OR date_trunc('month', w.day) <= date_trunc('month', ifr.end_date))
                ORDER BY ifr.start_date DESC
                LIMIT 1
            ),
            (
                SELECT ug.maximum_flow_rate
                FROM sch_regulatory.usage_grant ug
                WHERE ug.external_id = w.external_id
                ORDER BY ug.start_date DESC
                LIMIT 1
            ),
            0
        ) AS inst_flow_rate
    FROM with_lag w
)
SELECT
    f.external_id,
    f.day,
    CASE
        WHEN f.prev_last IS NULL THEN GREATEST(f.last_value, 0)
        ELSE GREATEST(f.last_value - f.prev_last, 0)
    END AS daily_pulse_diff,
    ROUND(
        LEAST(
            GREATEST(EXTRACT(EPOCH FROM (f.last_ts - f.first_ts)) / 3600.0, 0),
            24
        )::numeric, 3
    ) AS daily_op_hours,
    f.inst_flow_rate,
    (
        CASE
            WHEN f.prev_last IS NULL THEN GREATEST(f.last_value, 0)
            ELSE GREATEST(f.last_value - f.prev_last, 0)
        END
    ) * f.inst_flow_rate AS calculated_daily_measure
FROM with_flow f;

CREATE UNIQUE INDEX IF NOT EXISTS ux_mv_daily_agg_ext_day
    ON sch_view.mv_daily_agg (external_id, day);

CREATE INDEX IF NOT EXISTS idx_usage_grant_loc_dates
    ON sch_regulatory.usage_grant (location_id, start_date, end_date);

CREATE INDEX IF NOT EXISTS idx_ugm_grant_month
    ON sch_regulatory.usage_grant_monthly (usage_grant_id, month);

-- mv_monthly_agg depends on mv_daily_agg and is dropped by the CASCADE above.
-- Recreate it here so it reflects the corrected daily data.
CREATE MATERIALIZED VIEW sch_view.mv_monthly_agg AS
SELECT
    d.external_id,
    date_trunc('month', d.day::timestamp)::date AS ym,
    SUM(d.calculated_daily_measure)             AS monthly_volume,
    SUM(d.daily_op_hours)                       AS monthly_op_hours
FROM sch_view.mv_daily_agg d
GROUP BY
    d.external_id,
    date_trunc('month', d.day::timestamp)::date;

-- No CONCURRENTLY: the MV was just created in this same transaction.
CREATE UNIQUE INDEX IF NOT EXISTS ux_mv_monthly_agg_ext_ym
    ON sch_view.mv_monthly_agg (external_id, ym);

-- vw_daily_calculated_final (sch_monitoring) also depends on mv_daily_agg via LEFT JOIN
-- and is dropped by the CASCADE above. Recreate it identical to V32.
CREATE OR REPLACE VIEW sch_monitoring.vw_daily_calculated_final AS
WITH base AS (
    SELECT
        cr.external_id,
        cr.dt_reading::date  AS day,
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
    END                                                               AS source,
    o.updated_by                                                      AS override_updated_by,
    o.updated_at                                                      AS override_updated_at
FROM hours_horimeter h
    LEFT JOIN sch_monitoring.daily_operation_hours_override o
              ON o.external_id = h.external_id AND o.day = h.day
    LEFT JOIN sch_view.mv_daily_agg mv
              ON mv.external_id = h.external_id AND mv.day = h.day;
