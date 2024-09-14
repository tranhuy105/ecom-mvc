package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Category;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category rootCategory;
    private Category childCategory;
    private Category grandChildCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rootCategory = new Category();
        rootCategory.setId(1);
        rootCategory.setEnabled(true);

        childCategory = new Category();
        childCategory.setId(2);
        childCategory.setParent(rootCategory);
        childCategory.setEnabled(true);

        grandChildCategory = new Category();
        grandChildCategory.setId(3);
        grandChildCategory.setParent(childCategory);
        grandChildCategory.setEnabled(true);
    }

    @Test
    void testFindAllRoot() {
        when(categoryRepository.findAllRootCategory()).thenReturn(List.of(rootCategory));

        List<Category> result = categoryService.findAllRoot();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(rootCategory));
    }

    @Test
    void testFindByAlias() {
        when(categoryRepository.findByAlias("root")).thenReturn(Optional.of(rootCategory));

        Category result = categoryService.findByAlias("root");

        assertNotNull(result);
        assertEquals(rootCategory, result);
    }

    @Test
    void testFindByAlias_NotFound() {
        when(categoryRepository.findByAlias("nonexistent")).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            categoryService.findByAlias("nonexistent");
        });

        assertEquals("Category not found", thrown.getMessage());
    }

    @Test
    void testGetBreadcrumbTrail() {
        List<Category> categories = new ArrayList<>();
        categories.add(grandChildCategory);
        categories.add(childCategory);
        categories.add(rootCategory);

        when(categoryRepository.findCategoryPathById(grandChildCategory.getId())).thenReturn(categories);

        List<Category> result = categoryService.getBreadcrumbTrail(grandChildCategory);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(rootCategory, result.get(0));
        assertEquals(childCategory, result.get(1));
        assertEquals(grandChildCategory, result.get(2));
    }


    @Test
    void testGetDescendent() {
        when(categoryRepository.findAllDescendent(rootCategory.getId())).thenReturn(List.of(childCategory.getId(), grandChildCategory.getId()));

        List<Integer> result = categoryService.getDescendent(rootCategory.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(childCategory.getId()));
        assertTrue(result.contains(grandChildCategory.getId()));
    }
}
