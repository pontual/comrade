CREATE TABLE IF NOT EXISTS sch_regulatory.usage_grant (
    id BIGSERIAL PRIMARY KEY,
    location_id BIGSERIAL NOT NULL,
    external_id BIGINT NOT NULL,
    identifier VARCHAR(100) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    total_duration NUMERIC(15,2),
    total_volume NUMERIC(15,2),
    maximum_flow_rate NUMERIC(15,2),

    CONSTRAINT fk_grant_location
        FOREIGN KEY (location_id)
        REFERENCES sch_customer.location(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_usage_grant_location_id ON sch_regulatory.usage_grant (location_id);
CREATE INDEX IF NOT EXISTS idx_usage_grant_external_id ON sch_regulatory.usage_grant (external_id);
CREATE INDEX IF NOT EXISTS idx_usage_grant_identifier ON sch_regulatory.usage_grant (identifier);
CREATE INDEX IF NOT EXISTS idx_usage_grant_dates ON sch_regulatory.usage_grant (start_date, end_date);