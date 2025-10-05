CREATE TABLE IF NOT EXISTS sch_user.account_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    person_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (person_id) REFERENCES sch_person.person(id)
);

CREATE INDEX IF NOT EXISTS idx_username_account_user
    ON sch_user.account_user (username);