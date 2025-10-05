CREATE TABLE IF NOT EXISTS sch_person.person (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    document    VARCHAR(20) NOT NULL UNIQUE,
    email       VARCHAR(255),
    phone       VARCHAR(20),
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_document_person
    ON sch_person.person (document);