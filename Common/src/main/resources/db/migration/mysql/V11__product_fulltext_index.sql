ALTER TABLE products DROP INDEX idx_product_name;

ALTER TABLE products ADD FULLTEXT INDEX fts_products (name, short_description)