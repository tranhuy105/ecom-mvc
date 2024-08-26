ALTER TABLE products ADD COLUMN default_price DECIMAL(10, 2) DEFAULT 0.00;
ALTER TABLE products ADD COLUMN default_discount DECIMAL(5, 2) DEFAULT 0.00;

CREATE INDEX idx_default_price ON products (default_price);


