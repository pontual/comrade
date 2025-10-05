CREATE TABLE IF NOT EXISTS sch_customer.requester (
    id BIGSERIAL PRIMARY KEY,
    external_id BIGINT,
    name VARCHAR(255) NOT NULL,
    company_name VARCHAR(255),
    cnpj VARCHAR(14),
    cpf VARCHAR(11),
    rg VARCHAR(20),
    cellphone VARCHAR(11),
    phone VARCHAR(11),
    contact_name VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(100),
    number VARCHAR(10),
    neighborhood VARCHAR(60),
    zip_code VARCHAR(8),
    complement VARCHAR(30),
    state INTEGER,
    city INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_customer_external_id ON sch_customer.requester (external_id);
CREATE INDEX IF NOT EXISTS idx_customer_name ON sch_customer.requester (name);
CREATE INDEX IF NOT EXISTS idx_customer_cpf ON sch_customer.requester (cpf);
CREATE INDEX IF NOT EXISTS idx_customer_cnpj ON sch_customer.requester (cnpj);
CREATE INDEX IF NOT EXISTS idx_customer_email ON sch_customer.requester (email);
