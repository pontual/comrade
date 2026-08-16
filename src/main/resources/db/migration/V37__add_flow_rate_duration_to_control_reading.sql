ALTER TABLE sch_monitoring.control_reading
    ADD COLUMN IF NOT EXISTS flow_rate numeric(10,3),
    ADD COLUMN IF NOT EXISTS duration integer;