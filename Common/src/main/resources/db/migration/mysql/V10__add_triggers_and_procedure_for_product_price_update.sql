DELIMITER //

CREATE PROCEDURE update_product_price_discount(productId INT)
BEGIN
    DECLARE min_price DECIMAL(10, 2);
    DECLARE min_discount DECIMAL(5, 2);

    SELECT price, discount_percent
    INTO min_price, min_discount
    FROM skus
    WHERE product_id = productId
    ORDER BY price - (price * discount_percent / 100)
    LIMIT 1;

    UPDATE products
    SET default_price = min_price,
        default_discount = min_discount
    WHERE id = productId;
END //

CREATE TRIGGER after_sku_insert
    AFTER INSERT ON skus
    FOR EACH ROW
BEGIN
    CALL update_product_price_discount(NEW.product_id);
END //


CREATE TRIGGER after_sku_update
    AFTER UPDATE ON skus
    FOR EACH ROW
BEGIN
    CALL update_product_price_discount(NEW.product_id);
END //


CREATE TRIGGER after_sku_delete
    AFTER DELETE ON skus
    FOR EACH ROW
BEGIN
    CALL update_product_price_discount(OLD.product_id);
END //

DELIMITER ;
