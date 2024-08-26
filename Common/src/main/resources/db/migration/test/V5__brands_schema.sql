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

INSERT INTO brands (id, name, logo) VALUES
(1, 'Acer', 'acer.jpg'),
(2, 'Apple', 'apple.jpg'),
(3, 'Samsung', 'samsung.jpg'),
(4, 'Dell', 'dell.jpg'),
(5, 'HP', 'hp.jpg'),
(6, 'Lenovo', 'lenovo.jpg'),
(7, 'Sony', 'sony.jpg'),
(8, 'LG', 'lg.jpg'),
(9, 'Microsoft', 'microsoft.jpg'),
(10, 'NVIDIA', 'nvidia.jpg'),
(11, 'Intel', 'intel.jpg'),
(12, 'Corsair', 'corsair.jpg'),
(13, 'Asus', 'asus.jpg'),
(14, 'MSI', 'msi.jpg'),
(15, 'Razer', 'razer.jpg');

INSERT INTO brand_categories (brand_id, category_id) VALUES
(4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10), (4, 11), (4, 12),
(5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10), (5, 11), (5, 12),
(6, 2), (6, 3), (6, 4), (6, 5), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10), (6, 11), (6, 12),
(13, 2), (13, 3), (13, 4), (13, 5), (13, 6), (13, 7), (13, 8), (13, 9), (13, 10), (13, 11), (13, 12),
(14, 2), (14, 4), (14, 5), (14, 6), (14, 7), (14, 9), (14, 10);