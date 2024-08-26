ALTER TABLE categories DROP CONSTRAINT fk_parent_category;

ALTER TABLE categories
ADD CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE;

INSERT INTO categories (id, name, alias, image, enabled, parent_id) VALUES
(1, 'Computers', 'computers', 'computers.jpg', TRUE, NULL),
(2, 'Desktops', 'desktops', 'desktops.jpg', TRUE, 1),
(3, 'Laptops', 'laptops', 'laptops.jpg', TRUE, 1),
(4, 'Computer Components', 'computer-components', 'computer-components.jpg', TRUE, 1),
(5, 'RAM', 'ram', 'ram.jpg', TRUE, 4),
(6, 'CPU', 'cpu', 'cpu.jpg', TRUE, 4),
(7, 'GPU (Graphics Cards)', 'gpu', 'gpu.jpg', TRUE, 4),
(8, 'Motherboards', 'motherboards', 'motherboards.jpg', TRUE, 4),
(9, 'Storage (HDD/SSD)', 'storage', 'storage.jpg', TRUE, 4),
(10, 'Power Supplies', 'power-supplies', 'power-supplies.jpg', TRUE, 4),
(11, 'Cooling Systems', 'cooling-systems', 'cooling-systems.jpg', TRUE, 4),
(12, 'Cases', 'cases', 'cases.jpg', TRUE, 4),
(13, 'Network Cards', 'network-cards', 'network-cards.jpg', TRUE, 4),
(14, 'Sound Cards', 'sound-cards', 'sound-cards.jpg', TRUE, 4),
(15, 'Mouse', 'mouse', 'mouse.jpg', TRUE, 4),
(16, 'Keyboards', 'keyboards', 'keyboards.jpg', TRUE, 4),
(17, 'Monitors', 'monitors', 'monitors.jpg', TRUE, 4);