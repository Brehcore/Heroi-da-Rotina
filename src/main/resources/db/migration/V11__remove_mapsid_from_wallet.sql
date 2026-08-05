-- 1. Cria o novo ID auto-incrementável na tb_wallet
ALTER TABLE tb_wallet ADD COLUMN new_id BIGSERIAL;

-- =====================================================================
-- 🚨 DERRUBAR AS CHAVES ESTRANGEIRAS PRIMEIRO!
-- Isso desliga a validação para podermos mexer nos dados em paz.
-- =====================================================================
ALTER TABLE tb_money_transaction DROP CONSTRAINT IF EXISTS fk_money_wallet;
ALTER TABLE tb_token_transaction DROP CONSTRAINT IF EXISTS fk_token_wallet;
ALTER TABLE tb_screen_time_config DROP CONSTRAINT IF EXISTS tb_screen_time_config_wallet_id_fkey;

-- =====================================================================
-- 🚨 AGORA SIM, ATUALIZAR OS DADOS DOS FILHOS
-- Atualiza as tabelas filhas para apontarem para o new_id
-- =====================================================================
UPDATE tb_money_transaction mt
SET wallet_id = w.new_id
FROM tb_wallet w
WHERE mt.wallet_id = w.user_id;

UPDATE tb_token_transaction tt
SET wallet_id = w.new_id
FROM tb_wallet w
WHERE tt.wallet_id = w.user_id;

UPDATE tb_screen_time_config stc
SET wallet_id = w.new_id
FROM tb_wallet w
WHERE stc.wallet_id = w.user_id;

-- =====================================================================
-- 🚨 RECRIAR A ESTRUTURA PRINCIPAL
-- =====================================================================
-- Remove a chave primária antiga da tb_wallet
ALTER TABLE tb_wallet DROP CONSTRAINT tb_wallet_pkey CASCADE;

-- Define a new_id como Chave Primária oficial e renomeia para 'id'
ALTER TABLE tb_wallet ADD PRIMARY KEY (new_id);
ALTER TABLE tb_wallet RENAME COLUMN new_id TO id;

-- Adiciona constraint UNIQUE no user_id para manter a regra de 1 carteira por usuário
ALTER TABLE tb_wallet ADD CONSTRAINT uk_wallet_user_id UNIQUE (user_id);

-- =====================================================================
-- 🚨 RELIGAR OS CÃES DE GUARDA (Chaves Estrangeiras)
-- =====================================================================
ALTER TABLE tb_money_transaction
    ADD CONSTRAINT fk_money_wallet
        FOREIGN KEY (wallet_id) REFERENCES tb_wallet(id) ON DELETE CASCADE;

ALTER TABLE tb_token_transaction
    ADD CONSTRAINT fk_token_wallet
        FOREIGN KEY (wallet_id) REFERENCES tb_wallet(id) ON DELETE CASCADE;

ALTER TABLE tb_screen_time_config
    ADD CONSTRAINT tb_screen_time_config_wallet_id_fkey
        FOREIGN KEY (wallet_id) REFERENCES tb_wallet(id) ON DELETE CASCADE;