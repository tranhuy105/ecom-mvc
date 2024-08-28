ALTER TABLE cart_items
DROP CONSTRAINT cart_items_ibfk_2;

ALTER TABLE cart_items
DROP COLUMN product_id;

ALTER TABLE cart_items
ADD COLUMN sku_id INT NOT NULL;

ALTER TABLE cart_items
ADD CONSTRAINT fk_sku_id
FOREIGN KEY (sku_id) REFERENCES skus(id) ON DELETE CASCADE;

CREATE INDEX idx_cart_items_sku_id ON cart_items(sku_id);
