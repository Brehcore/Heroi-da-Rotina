-- Adiciona a coluna para armazenar a cotação da ficha
ALTER TABLE tb_wallet
    ADD COLUMN token_quotation DOUBLE PRECISION;