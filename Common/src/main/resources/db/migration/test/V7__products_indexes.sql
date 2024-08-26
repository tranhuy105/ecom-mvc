CREATE INDEX idx_product_alias ON products (alias);
CREATE INDEX idx_product_name ON products (name);
CREATE INDEX idx_category_id ON products (category_id);
CREATE INDEX idx_brand_id ON products (brand_id);
CREATE INDEX idx_product_created_at ON products (created_at);
CREATE INDEX idx_product_updated_at ON products (updated_at);

CREATE INDEX idx_product_detail_product_id_name ON product_details (product_id);

CREATE INDEX idx_product_image_product_id ON product_images (product_id);

CREATE INDEX idx_sku_product_id ON skus (product_id);
CREATE INDEX idx_sku_sku_code ON skus (sku_code);
