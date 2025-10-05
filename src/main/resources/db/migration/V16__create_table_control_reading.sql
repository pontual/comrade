CREATE TABLE IF NOT EXISTS sch_monitoring.control_reading (
    id SERIAL PRIMARY KEY,
    control_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    external_id BIGINT NOT NULL,
    reading_value NUMERIC(10, 3),
    dt_reading TIMESTAMP,
    created_by VARCHAR(255),
    observation VARCHAR(255),
    status VARCHAR(100),
    tag VARCHAR(100),
    average VARCHAR(10),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_control_reading_control
      FOREIGN KEY (control_id)
          REFERENCES sch_monitoring.control(id)
          ON DELETE CASCADE,

    CONSTRAINT fk_control_reading_location
        FOREIGN KEY (location_id)
            REFERENCES sch_customer.location(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_control_reading_control_id ON sch_monitoring.control_reading (control_id);
CREATE INDEX IF NOT EXISTS idx_control_reading_external_id ON sch_monitoring.control_reading (external_id);
CREATE INDEX IF NOT EXISTS idx_control_reading_dt_reading ON sch_monitoring.control_reading (dt_reading);
CREATE INDEX IF NOT EXISTS idx_control_reading_tag ON sch_monitoring.control_reading (tag);