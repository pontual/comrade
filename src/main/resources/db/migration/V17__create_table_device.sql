CREATE TABLE IF NOT EXISTS sch_monitoring.device (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(100),
    identifier VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL,
    linked_patrimony VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_device_identifier ON sch_monitoring.device (identifier);
