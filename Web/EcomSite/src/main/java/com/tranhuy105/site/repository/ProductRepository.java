package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Product;
import com.tranhuy105.site.dto.ProductOverview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query(nativeQuery = true, value = "WITH RECURSIVE CategoryHierarchy AS (" +
                        "SELECT c.id, c.parent_id " +
                        "FROM categories c " +
                        "WHERE c.id = :categoryId " +
                        "UNION ALL " +
                        "SELECT child.id, child.parent_id " +
                        "FROM categories child " +
                        "JOIN CategoryHierarchy parent ON parent.id = child.parent_id" +
                    ") " +
                    "SELECT * FROM products WHERE category_id IN (SELECT id FROM CategoryHierarchy)")
    List<Product> findAllByCategory(Pageable pageable, Integer categoryId);

    @Query(nativeQuery = true, value = "SELECT * FROM products " +
            "WHERE (:keyword IS NULL OR MATCH(name, short_description) AGAINST (:keyword)) " +
            "AND (category_id IN :categoryIds) " +
            "AND (:brandId IS NULL OR brand_id = :brandId) " +
            "AND (:minPrice IS NULL OR products.default_price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR products.default_price <= :maxPrice) " +
            "AND enabled = TRUE")
    Page<Product> searchProductWithCategory(Pageable pageable,
                                @Param("keyword") String keyword,
                                @Param("categoryIds") List<Integer> categoryIds,
                                @Param("brandId") Integer brandId,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice);

    @Query(nativeQuery = true, value = "SELECT * FROM products " +
            "WHERE (:keyword IS NULL OR MATCH(name, short_description) AGAINST (:keyword)) " +
            "AND (:brandId IS NULL OR brand_id = :brandId) " +
            "AND (:minPrice IS NULL OR products.default_price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR products.default_price <= :maxPrice) " +
            "AND enabled = TRUE")
    Page<Product> searchProduct(Pageable pageable,
                                            @Param("keyword") String keyword,
                                            @Param("brandId") Integer brandId,
                                            @Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice);

    @EntityGraph(attributePaths = {"category", "brand", "images"})
    @Query("SELECT p FROM Product p WHERE p IN :products")
    List<Product> findAllFull(List<Product> products);

    @EntityGraph(attributePaths = {"category", "brand", "skus", "additionalDetails", "images"})
    @Query("SELECT p FROM Product p WHERE p.alias = :alias AND p.enabled = TRUE")
    Optional<Product> findByAlias(String alias);

    @Query(value = "SELECT p.id AS id, " +
            "p.name AS name, " +
            "p.alias AS alias, " +
            "p.short_description AS shortDescription, " +
            "p.default_price AS price, " +
            "p.default_discount AS discountPercent, " +
            "pi.name AS imagePath " +
            "FROM products p " +
            "LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.is_main = TRUE " +
            "WHERE p.enabled = TRUE AND p.category_id = :categoryId",
            nativeQuery = true)
    List<ProductOverview> findProductOverviewsByCategory(Integer categoryId, PageRequest pageRequest);

    @Query(value = "SELECT p.id AS id, " +
            "p.name AS name, " +
            "p.alias AS alias, " +
            "p.short_description AS shortDescription, " +
            "p.default_price AS price, " +
            "p.default_discount AS discountPercent, " +
            "pi.name AS imagePath " +
            "FROM products p " +
            "LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.is_main = TRUE " +
            "WHERE p.enabled = TRUE AND p.brand_id = :brandId",
            nativeQuery = true)
    List<ProductOverview> getBrandProduct(Integer brandId, PageRequest pageRequest);
}
