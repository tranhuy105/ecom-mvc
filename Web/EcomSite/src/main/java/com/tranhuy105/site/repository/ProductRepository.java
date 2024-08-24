package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query(nativeQuery = true, value = """
        WITH RECURSIVE CategoryHierarchy AS (
            SELECT c.id, c.parent_id
            FROM categories c
            WHERE c.id = :categoryId
            UNION ALL
            SELECT child.id, child.parent_id
            FROM categories child
            JOIN CategoryHierarchy parent ON parent.id = child.parent_id
        )
        SELECT * FROM products WHERE category_id IN (SELECT id FROM CategoryHierarchy)
    """)
    List<Product> findAllByCategory(Pageable pageable, Integer categoryId);

    @EntityGraph(attributePaths = {"category", "brand", "images"})
    @Query("SELECT p FROM Product p WHERE p IN :products")
    List<Product> findAllFull(List<Product> products);

}
