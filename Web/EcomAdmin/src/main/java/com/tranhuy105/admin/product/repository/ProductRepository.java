package com.tranhuy105.admin.product.repository;

import com.tranhuy105.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

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
}
