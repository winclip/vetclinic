ALTER TABLE pets
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX idx_pets_owner_active ON pets(owner_id, is_active);
