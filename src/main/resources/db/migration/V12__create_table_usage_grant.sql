CREATE TABLE IF NOT EXISTS sch_regulatory.usage_grant (
    id SERIAL PRIMARY KEY,
    location_id INTEGER,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    total_duration NUMERIC(10,2),
    total_volume NUMERIC(10,2),
    CONSTRAINT fk_grant_location
        FOREIGN KEY (location_id)
        REFERENCES sch_customer.location(id)
        ON DELETE CASCADE
);

ALTER TABLE sch_regulatory.usage_grant
    OWNER TO "UserPontual";

CREATE INDEX IF NOT EXISTS idx_usage_grant_location_id ON sch_regulatory.usage_grant (location_id);
CREATE INDEX IF NOT EXISTS idx_usage_grant_dates ON sch_regulatory.usage_grant (start_date, end_date);