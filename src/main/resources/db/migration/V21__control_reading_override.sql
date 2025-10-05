CREATE TABLE IF NOT EXISTS sch_monitoring.daily_operation_hours_override (
    id                    BIGSERIAL PRIMARY KEY,
    external_id           BIGINT         NOT NULL,
    day                   DATE           NOT NULL,
    daily_hours_override  NUMERIC(10,3)  NOT NULL,
    updated_by            VARCHAR(100)   NOT NULL,
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_override_ext_day
    ON sch_monitoring.daily_operation_hours_override (external_id, day);