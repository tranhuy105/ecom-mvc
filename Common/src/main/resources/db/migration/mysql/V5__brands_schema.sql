CREATE TABLE brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(45) NOT NULL UNIQUE,
    logo VARCHAR(128) NOT NULL
);

CREATE TABLE brand_categories (
    brand_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (brand_id, category_id),
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_brand_categories_brand_id ON brand_categories(brand_id);
CREATE INDEX idx_brand_categories_category_id ON brand_categories(category_id);