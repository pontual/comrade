CREATE MATERIALIZED VIEW sch_view.mv_daily_agg AS
WITH base AS (
    SELECT
        cr.external_id,
        (cr.dt_reading AT TIME ZONE 'America/Sao_Paulo')::date AS day,
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