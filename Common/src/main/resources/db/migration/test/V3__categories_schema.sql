CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE,
    alias VARCHAR(64) NOT NULL UNIQUE,
    parent_id INT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_category_name ON categories(name);
CREATE INDEX idx_category_alias ON categories(alias);
CREATE INDEX idx_category_parent_id ON categories(parent_id);
