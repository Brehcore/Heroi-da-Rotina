ALTER TABLE tb_screen_time_requests
    ADD COLUMN IF NOT EXISTS approval_token VARCHAR(36);

ALTER TABLE tb_screen_time_requests
    ADD CONSTRAINT uk_tb_screen_time_requests_approval_token
        UNIQUE (approval_token);