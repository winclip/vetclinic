ALTER TABLE doctors
    ADD COLUMN bio                   VARCHAR(2000),
    ADD COLUMN photo_url             VARCHAR(512),
    ADD COLUMN date_of_birth         DATE,
    ADD COLUMN years_of_experience   INTEGER,
    ADD CONSTRAINT chk_doctors_years_of_experience
        CHECK (years_of_experience IS NULL OR (years_of_experience >= 0 AND years_of_experience <= 100));
