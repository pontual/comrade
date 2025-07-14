CREATE TABLE IF NOT EXISTS sch_monitoring.control (
    id SERIAL PRIMARY KEY,
    location_id BIGINT,
    device_id CHAR(12) CHECK (device_id ~ '^[0-9]{12}$'),
    dt_device_activate TIMESTAMP NOT NULL,
    device_status VARCHAR(30),
    reading_value NUMERIC(10,3),
    dt_reading TIMESTAMP,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    observation VARCHAR(255),
    status VARCHAR(100),
    tag VARCHAR(100),
    average VARCHAR(10),
    CONSTRAINT fk_control_location
        FOREIGN KEY (location_id)
            REFERENCES sch_customer.location(id)
            ON DELETE SET NULL
);

ALTER TABLE  sch_monitoring.control
    OWNER TO "UserPontual";

CREATE INDEX IF NOT EXISTS idx_control_location_id ON sch_monitoring.control (location_id);
CREATE INDEX IF NOT EXISTS idx_control_device_id ON sch_monitoring.control (device_id);
CREATE INDEX IF NOT EXISTS idx_control_dt_reading ON sch_monitoring.control (dt_reading);
CREATE INDEX IF NOT EXISTS idx_control_tag ON sch_monitoring.control (tag);
