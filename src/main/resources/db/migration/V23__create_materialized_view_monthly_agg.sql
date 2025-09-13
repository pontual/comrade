CREATE MATERIALIZED VIEW sch_view.mv_monthly_agg AS
SELECT
    d.external_id,
    date_trunc('month', d.day::timestamp)::date AS ym,
    SUM(d.calculated_daily_measure) AS monthly_volume,
    SUM(d.daily_op_hours)           AS monthly_op_hours
FROM sch_view.mv_daily_agg d
GROUP BY
    d.external_id,
    date_trunc('month', d.day::timestamp)::date;