ALTER TABLE categories
DROP FOREIGN KEY fk_parent_category;

ALTER TABLE categories
ADD CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE;
