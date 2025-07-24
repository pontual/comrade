CREATE TABLE IF NOT EXISTS sch_monitoring.instantaneous_flow_rate (
    id SERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL,
    id_usage_grant BIGINT NOT NULL,
    instantaneous_flow_measurement NUMERIC(10,2),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    CONSTRAINT fk_flow_location
        FOREIGN KEY (location_id)
            REFERENCES sch_customer.location(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_flow_usage_grant
        FOREIGN KEY (id_usage_grant)
            REFERENCES sch_regulatory.usage_grant(id)
            ON DELETE CASCADE
);

ALTER TABLE sch_monitoring.instantaneous_flow_rate
    OWNER TO "UserPontual";

CREATE INDEX IF NOT EXISTS idx_flow_location_id ON sch_monitoring.instantaneous_flow_rate (location_id);
CREATE INDEX IF NOT EXISTS idx_flow_usage_grant_id ON sch_monitoring.instantaneous_flow_rate (id_usage_grant);
CREATE INDEX IF NOT EXISTS idx_flow_start_end_date ON sch_monitoring.instantaneous_flow_rate (start_date, end_date);
