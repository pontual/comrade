CREATE TABLE IF NOT EXISTS sch_regulatory.usage_grant_monthly (
    id SERIAL PRIMARY KEY,
    usage_grant_id INTEGER NOT NULL,
    month INTEGER NOT NULL CHECK (month BETWEEN 1 AND 12),
    flow_rate NUMERIC(15,2),
    hours_day NUMERIC(4,2),
    days_month NUMERIC(4,2),
    maximum_volume NUMERIC(15, 2),
      CONSTRAINT fk_usage_grant_monthly
          FOREIGN KEY (usage_grant_id)
              REFERENCES sch_regulatory.usage_grant(id)
              ON DELETE CASCADE,
      CONSTRAINT uq_usage_grant_monthly UNIQUE (usage_grant_id, month)
);

CREATE INDEX IF NOT EXISTS idx_grant_monthly_grant_id ON sch_regulatory.usage_grant_monthly (usage_grant_id);
CREATE INDEX IF NOT EXISTS idx_grant_monthly_month ON sch_regulatory.usage_grant_monthly (month);