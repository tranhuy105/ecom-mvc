ALTER TABLE orders ADD COLUMN shipping_order_code VARCHAR(64);
ALTER TABLE orders ADD COLUMN expected_delivery_time TIMESTAMP;