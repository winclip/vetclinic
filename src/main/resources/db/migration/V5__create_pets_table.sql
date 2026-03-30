CREATE TABLE pets (
    id          BIGSERIAL PRIMARY KEY,
    owner_id    BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    species     VARCHAR(32)  NOT NULL,
    breed       VARCHAR(100),
    sex         VARCHAR(16),
    date_of_birth DATE,
    notes       VARCHAR(2000),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_pets_species CHECK (species IN ('DOG', 'CAT', 'BIRD', 'RABBIT', 'RODENT', 'REPTILE', 'OTHER')),
    CONSTRAINT chk_pets_sex CHECK (sex IS NULL OR sex IN ('MALE', 'FEMALE', 'UNKNOWN'))
);

CREATE INDEX idx_pets_owner_id ON pets(owner_id);
