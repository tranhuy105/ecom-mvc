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
    "value" VARCHAR(255) NOT NULL,
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


-- Products
INSERT INTO products (id, name, alias, short_description, full_description, created_at, updated_at, enabled, category_id, brand_id) VALUES
(1, 'Dell XPS 15', 'dell-xps-15', '15-inch laptop with powerful specs', 'The Dell XPS 15 offers high performance with Intel Core i7, 16GB RAM, and 512GB SSD. Ideal for professionals.', NOW(), NOW(), TRUE, 3, 4),
(2, 'Apple MacBook Air', 'apple-macbook-air', 'Lightweight laptop with M1 chip', 'Apple MacBook Air with M1 chip, 8GB RAM, and 256GB SSD. Perfect for everyday tasks.', NOW(), NOW(), TRUE, 3, 2),
(3, 'HP Spectre x360', 'hp-spectre-x360', 'Convertible laptop with 4K display', 'HP Spectre x360 15-inch 2-in-1 laptop with 4K touchscreen, Intel Core i7, and 1TB SSD.', NOW(), NOW(), TRUE, 3, 5),
(4, 'Lenovo ThinkPad X1 Carbon', 'lenovo-thinkpad-x1-carbon', 'High-performance business laptop', 'Lenovo ThinkPad X1 Carbon Gen 9 with Intel Core i7, 16GB RAM, and 512GB SSD. Designed for professionals.', NOW(), NOW(), TRUE, 3, 6),
(5, 'Samsung Galaxy S21', 'samsung-galaxy-s21', 'Latest Samsung smartphone', 'Samsung Galaxy S21 with 8GB RAM and 128GB storage. Features a triple-camera system and Exynos 2100 chipset.', NOW(), NOW(), TRUE, 4, 3);

-- Product Images
INSERT INTO product_images (id, name, product_id) VALUES
(1, 'dell-xps-15-front.jpg', 1),
(2, 'dell-xps-15-side.jpg', 1),
(3, 'apple-macbook-air-front.jpg', 2),
(4, 'apple-macbook-air-back.jpg', 2),
(5, 'hp-spectre-x360-front.jpg', 3),
(6, 'hp-spectre-x360-side.jpg', 3),
(7, 'lenovo-thinkpad-x1-carbon-front.jpg', 4),
(8, 'lenovo-thinkpad-x1-carbon-side.jpg', 4),
(9, 'samsung-galaxy-s21-front.jpg', 5),
(10, 'samsung-galaxy-s21-back.jpg', 5);

-- Product Details
INSERT INTO product_details (id, name, "value", product_id) VALUES
(1, 'Processor', 'Intel Core i7', 1),
(2, 'RAM', '16GB', 1),
(3, 'Storage', '512GB SSD', 1),
(4, 'Display', '15-inch 4K', 1),
(5, 'Processor', 'Apple M1', 2),
(6, 'RAM', '8GB', 2),
(7, 'Storage', '256GB SSD', 2),
(8, 'Display', '13.3-inch Retina', 2),
(9, 'Processor', 'Intel Core i7', 3),
(10, 'RAM', '16GB', 3),
(11, 'Storage', '1TB SSD', 3),
(12, 'Display', '15-inch 4K touchscreen', 3),
(13, 'Processor', 'Intel Core i7', 4),
(14, 'RAM', '16GB', 4),
(15, 'Storage', '512GB SSD', 4),
(16, 'Display', '14-inch Full HD', 4),
(17, 'Processor', 'Exynos 2100', 5),
(18, 'RAM', '8GB', 5),
(19, 'Storage', '128GB', 5),
(20, 'Display', '6.2-inch Dynamic AMOLED', 5);

-- SKUs
INSERT INTO skus (id, sku_code, price, discount_percent, stock_quantity, length, width, height, weight, product_id) VALUES
-- Dell XPS 15
(1, 'DELL-XPS-15-1', 1499.99, 10.00, 50, 35.00, 23.00, 1.80, 2.00, 1),
(2, 'DELL-XPS-15-2', 1599.99, 12.00, 30, 35.00, 23.00, 1.80, 2.00, 1),

-- Apple MacBook Air
(3, 'APPLE-MBAIR-1', 999.99, 5.00, 30, 32.00, 22.00, 1.60, 1.20, 2),
(4, 'APPLE-MBAIR-2', 1099.99, 7.00, 20, 32.00, 22.00, 1.60, 1.20, 2),

-- HP Spectre x360
(5, 'HP-SPECTRE-1', 1299.99, 15.00, 25, 36.00, 24.00, 1.70, 1.80, 3),
(6, 'HP-SPECTRE-2', 1399.99, 17.00, 15, 36.00, 24.00, 1.70, 1.80, 3),

-- Lenovo ThinkPad X1 Carbon
(7, 'LENOVO-TPX1C-1', 1399.99, 8.00, 40, 34.00, 23.00, 1.80, 2.00, 4),
(8, 'LENOVO-TPX1C-2', 1499.99, 10.00, 35, 34.00, 23.00, 1.80, 2.00, 4),

-- Samsung Galaxy S21
(9, 'SAMSUNG-GS21-1', 799.99, 12.00, 100, 15.00, 7.00, 0.80, 0.20, 5),
(10, 'SAMSUNG-GS21-2', 849.99, 15.00, 80, 15.00, 7.00, 0.80, 0.20, 5);