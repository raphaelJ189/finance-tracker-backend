CREATE TABLE budgets (
    id           BIGSERIAL       PRIMARY KEY,
    user_id      BIGINT          NOT NULL REFERENCES users(id),
    category_id  BIGINT          NOT NULL REFERENCES categories(id),
    amount       DECIMAL(19,2)   NOT NULL,
    month        INTEGER         NOT NULL,
    year         INTEGER         NOT NULL,
    created_at   TIMESTAMP       NOT NULL,

    CONSTRAINT chk_budget_amount
        CHECK (amount > 0),

    CONSTRAINT chk_budget_month
        CHECK (month BETWEEN 1 AND 12),

    CONSTRAINT chk_budget_year
        CHECK (year BETWEEN 2000 AND 2100),

    CONSTRAINT uq_budget_user_category_month_year
        UNIQUE (user_id, category_id, month, year)
);

CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_user_month_year
    ON budgets(user_id, month, year);