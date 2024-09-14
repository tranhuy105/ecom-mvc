package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    void testFindAllRootCategory() {
        List<Category> rootCategories = categoryRepository.findAllRootCategory();
        assertNotNull(rootCategories);
        assertFalse(rootCategories.isEmpty());
        assertEquals(1, rootCategories.size());
        assertEquals("Computers", rootCategories.get(0).getName());
    }

    @Test
    void testFindByAlias() {
        Optional<Category> category = categoryRepository.findByAlias("computers");
        assertTrue(category.isPresent());
        assertEquals("Computers", category.get().getName());
    }

    @Test
    void testFindCategoryPathById() {
        List<Category> categoryPath = categoryRepository.findCategoryPathById(5);
        assertNotNull(categoryPath);
        assertFalse(categoryPath.isEmpty());
        assertEquals(3, categoryPath.size());
        assertEquals("RAM", categoryPath.get(0).getName());
        assertEquals("Computer Components", categoryPath.get(1).getName());
    }
}
