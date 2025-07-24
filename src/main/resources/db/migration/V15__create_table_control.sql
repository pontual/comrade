CREATE TABLE IF NOT EXISTS sch_monitoring.control (
    id SERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL,
    device_id VARCHAR(20) NOT NULL,
    device_status VARCHAR(30) NOT NULL,
    dt_device_activate TIMESTAMP NOT NULL,
    dt_device_deactivation TIMESTAMP,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_control_location
      FOREIGN KEY (location_id)
          REFERENCES sch_customer.location(id)
          ON DELETE CASCADE
);

ALTER TABLE sch_monitoring.control
    OWNER TO "UserPontual";

CREATE INDEX IF NOT EXISTS idx_control_location_id ON sch_monitoring.control (location_id);
CREATE INDEX IF NOT EXISTS idx_control_device_id ON sch_monitoring.control (device_id);