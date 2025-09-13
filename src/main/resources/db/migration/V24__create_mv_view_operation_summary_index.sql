CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS ux_mv_monthly_agg_ext_ym
    ON sch_view.mv_monthly_agg (external_id, ym);