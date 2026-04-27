CREATE TABLE users (
    id               BIGSERIAL       PRIMARY KEY,
    name             VARCHAR(100)    NOT NULL,
    email            VARCHAR(255)    NOT NULL UNIQUE,
    password         VARCHAR(255)    NOT NULL,
    role             VARCHAR(20)     NOT NULL DEFAULT 'USER',
    is_active        BOOLEAN         NOT NULL DEFAULT TRUE,
    profile_picture  VARCHAR(500),
    currency         VARCHAR(10)     NOT NULL DEFAULT 'TSH',
    created_at       TIMESTAMP       NOT NULL,
    updated_at       TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);