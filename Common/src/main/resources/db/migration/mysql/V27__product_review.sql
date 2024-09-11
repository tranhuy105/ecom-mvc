CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(5000) NOT NULL,
    rating INT NOT NULL,
    created_at DATETIME NOT NULL,

    customer_id INT NOT NULL,
    order_item_id INT NOT NULL,
    sku_id INT NOT NULL,

    CONSTRAINT fk_review_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_sku FOREIGN KEY (sku_id) REFERENCES skus(id) ON DELETE CASCADE,
    CONSTRAINT uq_customer_order_item UNIQUE (customer_id, order_item_id)
);

CREATE INDEX idx_review_customer ON reviews(customer_id);
CREATE INDEX idx_review_order_item ON reviews(order_item_id);
CREATE INDEX idx_review_sku ON reviews(sku_id);
