CREATE TABLE IF NOT EXISTS sch_configuration.function (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    last_update TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_function_name_enabled_true
    ON sch_configuration.function (name)
    WHERE enabled = true;