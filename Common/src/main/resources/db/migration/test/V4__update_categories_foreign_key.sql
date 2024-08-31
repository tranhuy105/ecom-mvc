ALTER TABLE categories DROP CONSTRAINT fk_parent_category;

ALTER TABLE categories
ADD CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE;

INSERT INTO categories (id, name, alias, enabled, parent_id) VALUES
(1, 'Computers', 'computers', TRUE, NULL),
(2, 'Desktops', 'desktops', TRUE, 1),
(3, 'Laptops', 'laptops', TRUE, 1),
(4, 'Computer Components', 'computer-components', TRUE, 1),
(5, 'RAM', 'ram', TRUE, 4),
(6, 'CPU', 'cpu', TRUE, 4),
(7, 'GPU (Graphics Cards)', 'gpu.jpg', TRUE, 4),
(8, 'Motherboards', 'motherboards', TRUE, 4),
(9, 'Storage (HDD/SSD)', 'storage', TRUE, 4),
(10, 'Power Supplies', 'power-supplies', TRUE, 4),
(11, 'Cooling Systems', 'cooling-systems', TRUE, 4),
(12, 'Cases', 'cases', TRUE, 4),
(13, 'Network Cards', 'network-cards', TRUE, 4),
(14, 'Sound Cards', 'sound-cards', TRUE, 4),
(15, 'Mouse', 'mouse', TRUE, 4),
(16, 'Keyboards', 'keyboards', TRUE, 4),
(17, 'Monitors', 'monitors', TRUE, 4);