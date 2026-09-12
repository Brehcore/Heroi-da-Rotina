CREATE TABLE tb_savings_goals (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    target_amount NUMERIC(12, 2) NOT NULL,
    current_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    target_date DATE,
    icon VARCHAR(50) DEFAULT 'TARGET',
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',
    minor_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_savings_goals_minor FOREIGN KEY (minor_id) REFERENCES tb_users(id) ON DELETE CASCADE
);

CREATE INDEX idx_savings_goals_minor_id ON tb_savings_goals(minor_id);