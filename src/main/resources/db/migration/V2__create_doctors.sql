CREATE TABLE doctors (
    id                  BIGSERIAL PRIMARY KEY,
    first_name          VARCHAR(100)  NOT NULL,
    last_name           VARCHAR(100)  NOT NULL,
    specialization      VARCHAR(255),
    phone               VARCHAR(32),
    email               VARCHAR(255),
    veterinary_license  VARCHAR(64),
    hired_on            DATE,
    is_active           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_doctors_email UNIQUE (email),
    CONSTRAINT uq_doctors_veterinary_license UNIQUE (veterinary_license)
);
