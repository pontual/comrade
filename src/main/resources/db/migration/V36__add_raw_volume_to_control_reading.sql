ALTER TABLE sch_monitoring.control_reading
    ADD COLUMN IF NOT EXISTS raw_volume numeric(14,3);