package com.tranhuy105.admin.category;

import com.tranhuy105.common.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @EntityGraph(attributePaths = {"children"})
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findByIdWithChildren(Integer id);

    @EntityGraph(attributePaths = {"children", "parent"})
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findByIdWithChildrenAndParent(Integer id);


    @EntityGraph(attributePaths = {"children", "parent"})
    @Query("SELECT c FROM Category c")
    List<Category> findAllWithChildrenAndParent();

    @Query("SELECT c.id FROM Category c WHERE c.name = :name OR c.alias = :alias")
    Optional<Integer> findByNameOrAlias(String name, String alias);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM categories WHERE id = :id")
    void delete(Integer id);
}
