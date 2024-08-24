package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE c.parent.id IS NULL AND c.enabled = true")
    List<Category> findAllRootCategory();

    @EntityGraph(attributePaths = {"children", "parent"})
    @Query("SELECT c FROM Category c WHERE c.alias = :alias AND c.enabled = TRUE")
    Optional<Category> findByAlias(String alias);

    @Transactional(readOnly = true)
    @Query(value = "WITH RECURSIVE CategoryPath (id, name, alias, enabled, image, parent_id, level) AS (" +
            "SELECT c.id, c.name, c.alias, c.enabled, c.image, c.parent_id, 0 AS level " +
            "FROM categories c " +
            "WHERE c.id = :id " +
            "UNION ALL " +
            "SELECT p.id, p.name, p.alias, p.enabled, p.image, p.parent_id, cp.level + 1 " +
            "FROM categories p " +
            "JOIN CategoryPath cp ON p.id = cp.parent_id " +
            ") " +
            "SELECT id, name, alias, enabled, image, parent_id, level " +
            "FROM CategoryPath", nativeQuery = true)
    List<Category> findCategoryPathById(@NonNull Integer id);

}
