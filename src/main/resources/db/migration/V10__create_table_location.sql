CREATE TABLE IF NOT EXISTS sch_customer.location (
    id SERIAL PRIMARY KEY,
    external_id BIGINT,
    description VARCHAR(255),
    requester_id INTEGER,
    location_id INTEGER,
    location VARCHAR(255),
    category_id INTEGER,
    category VARCHAR(100),
    type_tech_id INTEGER,
    type_tech VARCHAR(255),
    observation VARCHAR(255),
    brand_id INTEGER,
    brand VARCHAR(255),
    model_id INTEGER,
    model VARCHAR(255),
    serial VARCHAR(255),
    patrimony VARCHAR(255),
    tag VARCHAR(255),
    data_matrix VARCHAR(255),
    details VARCHAR(255),
    status INTEGER,
    included_at TIMESTAMP,
    updated_at TIMESTAMP,
    guarantee_until TIMESTAMP,
    situation VARCHAR(255),
    situation_id INTEGER
);

ALTER TABLE  sch_customer.location
    OWNER TO "UserPontual";

CREATE INDEX IF NOT EXISTS idx_customer_external_id ON sch_customer.location (external_id);
CREATE INDEX IF NOT EXISTS idx_customer_requester_id ON sch_customer.location (requester_id);