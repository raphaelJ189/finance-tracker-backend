CREATE TABLE categories (
    id           BIGSERIAL       PRIMARY KEY,
    user_id      BIGINT          NOT NULL REFERENCES users(id),
    name         VARCHAR(100)    NOT NULL,
    type         VARCHAR(10)     NOT NULL,
    color        VARCHAR(7)      NOT NULL DEFAULT '#6366F1',
    icon         VARCHAR(50)     NOT NULL DEFAULT 'tag',
    description  VARCHAR(255),
    is_active    BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP       NOT NULL,

    CONSTRAINT chk_category_type
        CHECK (type IN ('INCOME', 'EXPENSE')),

    CONSTRAINT uq_user_category_name_type
        UNIQUE (user_id, name, type)
);

CREATE INDEX idx_categories_user_id ON categories(user_id);