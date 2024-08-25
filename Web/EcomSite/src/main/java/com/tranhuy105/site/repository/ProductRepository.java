package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Product;
import org.springframework.data.domain.Page;
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
            "AND (:categoryId IS NULL OR category_id = :categoryId) " +
            "AND (:brandId IS NULL OR brand_id = :brandId) " +
            "AND (:minPrice IS NULL OR products.default_price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR products.default_price <= :maxPrice) " +
            "AND enabled = TRUE")
    Page<Product> searchProduct(Pageable pageable,
                                @Param("keyword") String keyword,
                                @Param("categoryId") Integer categoryId,
                                @Param("brandId") Integer brandId,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice);

    @EntityGraph(attributePaths = {"category", "brand", "images"})
    @Query("SELECT p FROM Product p WHERE p IN :products")
    List<Product> findAllFull(List<Product> products);

    @EntityGraph(attributePaths = {"category", "brand", "skus", "additionalDetails", "images"})
    @Query("SELECT p FROM Product p WHERE p.alias = :alias")
    Optional<Product> findByAlias(String alias);
}
