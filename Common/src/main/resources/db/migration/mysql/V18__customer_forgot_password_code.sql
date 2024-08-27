ALTER TABLE customers
ADD COLUMN forgot_password_code VARCHAR(32) UNIQUE;