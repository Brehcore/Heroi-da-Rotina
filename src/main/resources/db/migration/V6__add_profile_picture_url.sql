-- 1. Adiciona a coluna de foto de perfil nos usuários
ALTER TABLE tb_users
    ADD COLUMN IF NOT EXISTS profile_picture_url VARCHAR(500);

-- 2. Adiciona a coluna de foto de perfil nas famílias
ALTER TABLE tb_family
    ADD COLUMN IF NOT EXISTS profile_picture_url VARCHAR(500);

-- 3. Atualiza os usuários antigos gerando um robô (bottts) com o nome deles
UPDATE tb_users
SET profile_picture_url = 'https://api.dicebear.com/8.x/bottts/svg?seed=' || REPLACE(name, ' ', '')
WHERE profile_picture_url IS NULL;

-- 4. Atualiza as famílias antigas gerando um emoji (fun-emoji) com o nome delas
UPDATE tb_family
SET profile_picture_url = 'https://api.dicebear.com/8.x/fun-emoji/svg?seed=' || REPLACE(family_name, ' ', '')
WHERE profile_picture_url IS NULL;