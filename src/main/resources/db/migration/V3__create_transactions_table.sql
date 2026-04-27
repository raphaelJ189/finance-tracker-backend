CREATE TABLE transactions (
    id                BIGSERIAL       PRIMARY KEY,
    user_id           BIGINT          NOT NULL REFERENCES users(id),
    category_id       BIGINT          NOT NULL REFERENCES categories(id),
    amount            DECIMAL(19,4)   NOT NULL,
    type              VARCHAR(10)     NOT NULL,
    description       VARCHAR(500)    NOT NULL,
    reference_number  VARCHAR(100),
    transaction_date  DATE            NOT NULL,
    notes             VARCHAR(1000),
    is_deleted        BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP       NOT NULL,
    updated_at        TIMESTAMP,

    CONSTRAINT chk_transaction_type
        CHECK (type IN ('INCOME', 'EXPENSE')),

    CONSTRAINT chk_transaction_amount
        CHECK (amount > 0)
);

CREATE INDEX idx_transactions_user_id
    ON transactions(user_id);

CREATE INDEX idx_transactions_category_id
    ON transactions(category_id);

CREATE INDEX idx_transactions_date
    ON transactions(transaction_date);

CREATE INDEX idx_transactions_user_date
    ON transactions(user_id, transaction_date);