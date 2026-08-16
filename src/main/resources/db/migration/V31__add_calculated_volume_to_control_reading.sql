ALTER TABLE sch_monitoring.control_reading
    ADD COLUMN IF NOT EXISTS calculated_volume NUMERIC(14, 3);