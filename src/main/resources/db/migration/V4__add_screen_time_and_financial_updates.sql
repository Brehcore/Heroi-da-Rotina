-- 1. Atualizações na tabela da Carteira (À prova de falhas)
ALTER TABLE tb_wallet
    ADD COLUMN IF NOT EXISTS token_quotation DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS interest_rate DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS interest_enabled BOOLEAN;

-- 2. Criação da tabela de Configuração de Tempo de Tela
CREATE TABLE IF NOT EXISTS tb_screen_time_config (
                                                     id BIGSERIAL PRIMARY KEY,
    -- CORREÇÃO AQUI: Apontando para user_id em vez de id
                                                     wallet_id BIGINT REFERENCES tb_wallet(user_id),
    minutes_per_token INT,
    monday_limit INT,
    tuesday_limit INT,
    wednesday_limit INT,
    thursday_limit INT,
    friday_limit INT,
    saturday_limit INT,
    sunday_limit INT
    );

-- 3. Criação da tabela de Solicitações de Tempo de Tela
CREATE TABLE IF NOT EXISTS tb_screen_time_requests (
                                                       id BIGSERIAL PRIMARY KEY,
                                                       minor_id BIGINT REFERENCES tb_users(id),
    requested_minutes INT,
    token_cost INT,
    screen_status VARCHAR(20) NOT NULL,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_by_id BIGINT REFERENCES tb_users(id)
    );