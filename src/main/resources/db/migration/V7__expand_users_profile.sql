ALTER TABLE users
    ADD COLUMN email     VARCHAR(255),
    ADD COLUMN full_name VARCHAR(200);

CREATE UNIQUE INDEX uq_users_email ON users (email) WHERE email IS NOT NULL;
