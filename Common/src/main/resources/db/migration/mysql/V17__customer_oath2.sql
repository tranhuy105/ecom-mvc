ALTER TABLE customers
ADD COLUMN authentication_type VARCHAR(10) NOT NULL DEFAULT 'EMAIL';

ALTER TABLE customers
ADD CONSTRAINT chk_authentication_type
CHECK (authentication_type IN ('EMAIL', 'GOOGLE', 'FACEBOOK'));
