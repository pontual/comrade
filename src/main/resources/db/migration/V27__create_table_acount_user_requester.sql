CREATE TABLE IF NOT EXISTS sch_user.account_user_requester (
    id SERIAL PRIMARY KEY,
    user_id      INTEGER NOT NULL,
    requester_id INTEGER NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_account_user_requester UNIQUE (user_id, requester_id),

    CONSTRAINT fk_aur_user
       FOREIGN KEY (user_id)
           REFERENCES sch_user.account_user(id)
           ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_aur_user ON sch_user.account_user_requester (user_id);
