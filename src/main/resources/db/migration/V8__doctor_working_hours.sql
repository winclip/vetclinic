CREATE TABLE doctor_working_hours (
    id              BIGSERIAL PRIMARY KEY,
    doctor_id       BIGINT        NOT NULL REFERENCES doctors (id) ON DELETE CASCADE,
    day_of_week     SMALLINT      NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time      TIME          NOT NULL,
    end_time        TIME          NOT NULL,
    CONSTRAINT chk_doctor_working_hours_interval CHECK (end_time > start_time)
);

CREATE INDEX idx_doctor_working_hours_doctor_day ON doctor_working_hours (doctor_id, day_of_week);
