CREATE TABLE appointments (
    id          BIGSERIAL PRIMARY KEY,
    doctor_id   BIGINT        NOT NULL REFERENCES doctors (id) ON DELETE RESTRICT,
    pet_id      BIGINT        NOT NULL REFERENCES pets (id) ON DELETE RESTRICT,
    starts_at   TIMESTAMPTZ   NOT NULL,
    ends_at     TIMESTAMPTZ   NOT NULL,
    status      VARCHAR(32)   NOT NULL DEFAULT 'SCHEDULED',
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_appointments_interval CHECK (ends_at > starts_at),
    CONSTRAINT chk_appointments_status CHECK (status IN ('SCHEDULED', 'CANCELLED', 'COMPLETED'))
);

CREATE INDEX idx_appointments_doctor_starts ON appointments (doctor_id, starts_at);
CREATE INDEX idx_appointments_pet ON appointments (pet_id);
