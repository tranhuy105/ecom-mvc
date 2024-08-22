CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    alias VARCHAR(255) NOT NULL UNIQUE,
    short_description VARCHAR(512) NOT NULL,
    full_description VARCHAR(4096) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE,
    category_id INT,
    brand_id INT,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL
);

CREATE TABLE product_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    product_id INT NOT NULL,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE product_images (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    product_id INT NOT NULL,
    CONSTRAINT fk_product_image FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE skus (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sku_code VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_percent DECIMAL(5, 2) DEFAULT 0.00,
    stock_quantity INT NOT NULL,
    length DECIMAL(10, 2) DEFAULT 0.00,
    width DECIMAL(10, 2) DEFAULT 0.00,
    height DECIMAL(10, 2) DEFAULT 0.00,
    weight DECIMAL(10, 2) DEFAULT 0.00,
    product_id INT NOT NULL,
    CONSTRAINT fk_sku_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);