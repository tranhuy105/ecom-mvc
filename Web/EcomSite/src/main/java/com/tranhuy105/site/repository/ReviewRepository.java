package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Review;
import com.tranhuy105.site.dto.ReviewDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("SELECT r.createdAt AS createdAt," +
            "r.content AS content, " +
            "r.rating AS rating," +
            "CONCAT(c.firstName, ' ', c.lastName) AS customerName," +
            "c.profilePictureUrl AS customerAvatar," +
            "sk.skuCode AS productVariation " +
            "FROM Review r " +
            "LEFT JOIN r.sku sk " +
            "LEFT JOIN r.customer c " +
            "WHERE sk.product.id = :productId")
    List<ReviewDTO> findAllReviewByProductId(Integer productId, Pageable pageable);

    @Query("SELECT COUNT(r), COALESCE(AVG(r.rating), 0), " +
            "COALESCE(SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END), 0) " +
            "FROM Review r WHERE r.sku.product.id = :productId")
    List<Object[]> getReviewStatsByProductId(Integer productId);
}
