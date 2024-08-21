package com.tranhuy105.admin.brand;


import com.tranhuy105.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    @Query("SELECT b FROM Brand b WHERE b.name LIKE %:q%")
    Page<Brand> findAllWithFilter(Pageable pageable, String q);

    @EntityGraph(attributePaths = {"categories"})
    @Query("SELECT b FROM Brand b WHERE b IN :brands")
    List<Brand> findWithCategories(List<Brand> brands);
}
