CREATE TABLE tb_password_reset_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    token_expiration TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tb_users(id) ON DELETE CASCADE
);