-- 1. Cria a nova tabela de relacionamento Muitos-para-Muitos
CREATE TABLE tb_users_families (
                                   user_id BIGINT NOT NULL,
                                   family_id BIGINT NOT NULL,
                                   PRIMARY KEY (user_id, family_id),
                                   CONSTRAINT fk_users_families_user FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE,
                                   CONSTRAINT fk_users_families_family FOREIGN KEY (family_id) REFERENCES tb_family (id) ON DELETE CASCADE
);

-- 2. Migração de Dados (Salva a vida!):
-- Copia os vínculos existentes da coluna velha para a tabela nova
INSERT INTO tb_users_families (user_id, family_id)
SELECT id, family_id FROM tb_users WHERE family_id IS NOT NULL;

-- 3. Limpeza: Agora que os dados estão a salvo, apagamos a coluna antiga da tabela de usuários
ALTER TABLE tb_users DROP COLUMN family_id;