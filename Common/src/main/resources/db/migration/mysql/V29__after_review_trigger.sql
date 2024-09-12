DELIMITER //

CREATE PROCEDURE UpdateProductRatings(IN productId INT)
BEGIN
    DECLARE avgRating DECIMAL(3,2);
    DECLARE reviewCount INT;

    SELECT
        IFNULL(AVG(r.rating), 0) AS avg_rating,
        COUNT(r.id) AS review_count
    INTO avgRating, reviewCount
    FROM reviews r
    JOIN skus s ON r.sku_id = s.id
    WHERE s.product_id = productId;

    UPDATE products
    SET rating = avgRating, reviews_count = reviewCount
    WHERE id = productId;
END//

CREATE TRIGGER after_review_insert
AFTER INSERT ON reviews
FOR EACH ROW
BEGIN
    CALL UpdateProductRatings(
        (SELECT product_id FROM skus WHERE id = NEW.sku_id)
    );
END//

CREATE TRIGGER after_review_update
AFTER UPDATE ON reviews
FOR EACH ROW
BEGIN
    CALL UpdateProductRatings(
        (SELECT product_id FROM skus WHERE id = NEW.sku_id)
    );
END//

CREATE TRIGGER after_review_delete
AFTER DELETE ON reviews
FOR EACH ROW
BEGIN
    CALL UpdateProductRatings(
        (SELECT product_id FROM skus WHERE id = OLD.sku_id)
    );
END//

DELIMITER ;