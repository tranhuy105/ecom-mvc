package com.tranhuy105.admin.repository;

import com.tranhuy105.admin.dto.ProductOverviewDTO;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.common.entity.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @EntityGraph(attributePaths = {"category", "brand", "skus", "additionalDetails", "images"})
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdFull(Integer id);

    @EntityGraph(attributePaths = {"category", "brand", "images"})
    @Query("SELECT p FROM Product p WHERE p IN :products")
    List<Product> findAllFull(List<Product> products);

    @Query("SELECT p FROM Product p WHERE (:search IS NULL OR p.name LIKE %:search%) AND (:categoryId IS NULL) OR p.category.id = :categoryId")
    Page<Product> findAll(@NonNull Pageable pageable, String search, Integer categoryId);

    @Query(nativeQuery = true, value = "SELECT " +
            "p.id AS id, " +
            "pi.name AS imagePath," +
            "p.name AS name," +
            "c.name AS category," +
            "c.id AS categoryId," +
            "b.name AS brand," +
            "b.id AS brandId," +
            "p.default_price AS price, " +
            "p.default_discount AS discountPercent," +
            "p.enabled AS enabled " +
            "FROM products p " +
            "LEFT JOIN product_images pi ON pi.product_id = p.id AND pi.is_main " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "LEFT JOIN brands b ON p.brand_id = b.id  " +
            "WHERE (:search IS NULL OR MATCH(p.name, p.short_description) AGAINST (:search)) " +
            "AND ((:categoryId IS NULL) OR p.category_id = :categoryId) " +
            "AND ((:brandId IS NULL) OR p.brand_id = :brandId) " +
            "AND ((:minPrice IS NULL) OR p.default_price >= :minPrice) " +
            "AND ((:maxPrice IS NULL) OR p.default_price <= :maxPrice)" +
            "AND ((:enabled IS NULL) OR p.enabled = :enabled)")
    Page<ProductOverviewDTO> findAllTest(@NonNull Pageable pageable,
                                         String search,
                                         Integer categoryId,
                                         Integer brandId,
                                         BigDecimal minPrice,
                                         BigDecimal maxPrice,
                                         Boolean enabled);

    @Query("SELECT p.id FROM Product p WHERE p.alias = :alias")
    Optional<Integer> findByAliasMin(String alias);

    @Query("SELECT pi.name FROM ProductImage pi WHERE pi.product.id = :id")
    List<String> findAllProductImageNames(Integer id);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM products WHERE id = :id")
    void delete(Integer id);
}
