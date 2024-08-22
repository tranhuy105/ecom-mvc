package com.tranhuy105.admin.brand;


import com.tranhuy105.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    @Query("SELECT b FROM Brand b WHERE b.name LIKE %:q%")
    Page<Brand> findAllWithFilter(Pageable pageable, String q);

    @EntityGraph(attributePaths = {"categories"})
    @Query("SELECT b FROM Brand b WHERE b IN :brands")
    List<Brand> findWithCategories(List<Brand> brands);

    @Query("SELECT b.id FROM Brand b WHERE b.name = :name")
    Optional<Integer> findByName(String name);

    @NonNull
    @EntityGraph(attributePaths = {"categories"})
    @Query("SELECT b FROM Brand b WHERE b.id = :id")
    Optional<Brand> findById(@NonNull Integer id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM brands WHERE id = :id")
    void delete(Integer id);
}
