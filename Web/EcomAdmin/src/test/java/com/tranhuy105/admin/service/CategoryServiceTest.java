package com.tranhuy105.admin.service;

import com.tranhuy105.admin.repository.CategoryRepository;
import com.tranhuy105.admin.service.impl.CategoryServiceImpl;
import com.tranhuy105.admin.utils.FileUploadUtil;
import com.tranhuy105.common.entity.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    void testGetPageSize() {
        assertEquals(30, categoryService.getPageSize());
    }

    @Test
    void testFindAllWithSearch() {
        List<Category> allCategories = List.of(
                new Category(1, "Category1", "cat1", true, null, new ArrayList<>()),
                new Category(2, "Category2", "cat2", true, null, new ArrayList<>())
        );
        List<Category> filteredCategories = List.of(allCategories.get(0));

        when(categoryRepository.findAllWithChildrenAndParent()).thenReturn(allCategories);

        Page<Category> result = categoryService.findAll(1, "Category1");

        assertEquals(1, result.getContent().size());
        assertEquals("Category1", result.getContent().get(0).getName());
    }

    @Test
    void testFindAllWithoutSearch() {
        List<Category> allCategories = List.of(
                new Category(1, "Category1", "cat1", true, null, new ArrayList<>()),
                new Category(2, "Category2", "cat2", true, null, new ArrayList<>())
        );

        when(categoryRepository.findAllWithChildrenAndParent()).thenReturn(allCategories);

        Page<Category> result = categoryService.findAll(1, null);

        assertEquals(2, result.getContent().size());
    }

    @Test
    void testFindById() {
        Category category = new Category(1, "Category1", "cat1", true, null, null);

        when(categoryRepository.findByIdWithChildrenAndParent(1)).thenReturn(Optional.of(category));

        Category result = categoryService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testFindByIdNotFound() {
        when(categoryRepository.findByIdWithChildrenAndParent(1)).thenReturn(Optional.empty());

        Category result = categoryService.findById(1);

        assertNull(result);
    }

    @Test
    void testSaveWithImage() throws IOException {
        Category category = new Category();
        category.setId(1);
        category.setName("Category1");
        category.setAlias("cat1");
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("image.png");

        try (MockedStatic<FileUploadUtil> mockedStatic = Mockito.mockStatic(FileUploadUtil.class)) {
            mockedStatic.when(() -> FileUploadUtil.validateAndGetImageFilename(file)).thenReturn("image.png");
            doNothing().when(FileUploadUtil.class);
            FileUploadUtil.saveFile(anyString(), anyString(), eq(file));

            when(categoryRepository.save(category)).thenReturn(category);

            Category result = categoryService.save(category, file);

            assertEquals(category, result);
            verify(categoryRepository).save(category);
        }
    }

    @Test
    void testSaveWithoutImage() throws IOException {
        Category category = new Category();
        category.setName("Category1");
        category.setAlias("cat1");
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        try (MockedStatic<FileUploadUtil> mockedStatic = Mockito.mockStatic(FileUploadUtil.class)) {
            when(categoryRepository.save(category)).thenReturn(category);

            Category result = categoryService.save(category, file);

            assertEquals(category, result);
            verify(categoryRepository).save(category);
        }
    }

    @Test
    void testFindAllWithHierarchy() {
        List<Category> categories = List.of(
                new Category(1, "Category1", "cat1", true, null, new ArrayList<>()),
                new Category(2, "Category2", "cat2", true, null, new ArrayList<>())
        );

        when(categoryRepository.findAllWithChildrenAndParent()).thenReturn(categories);

        List<Category> result = categoryService.findAllWithHierarchy();

        assertEquals(2, result.size());
        verify(categoryRepository).findAllWithChildrenAndParent();
    }

    @Test
    void testFindAllById() {
        List<Category> categories = List.of(
                new Category(1, "Category1", "cat1", true, null, new ArrayList<>()),
                new Category(2, "Category2", "cat2", true, null, new ArrayList<>())
        );

        when(categoryRepository.findAllById(List.of(1, 2))).thenReturn(categories);

        List<Category> result = categoryService.findAllById(List.of(1, 2));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(1)));
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(2)));
    }

    @Test
    void testUpdateCategoryInCache() {
        Category category = new Category(1, "Category1", "cat1", true, null, new ArrayList<>());

        when(categoryService.findAllWithHierarchy()).thenReturn(List.of(category));

        categoryService.updateCategoryInCache(category);

        List<Category> cachedCategories = categoryService.findAllWithHierarchy();
        assertTrue(cachedCategories.stream().anyMatch(c -> c.getId().equals(1)));
    }

    @Test
    void testDelete() {
        Category category = new Category();
        category.setId(1);

        when(categoryRepository.findByIdWithChildrenAndParent(1)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(anyInt());

        boolean result = categoryService.delete(1);

        assertTrue(result);
        verify(categoryRepository).delete(1);
    }


    @Test
    void testDeleteNotFound() {
        when(categoryRepository.findByIdWithChildrenAndParent(1)).thenReturn(Optional.empty());

        boolean result = categoryService.delete(1);

        assertFalse(result);
        verify(categoryRepository, never()).delete(anyInt());
    }
}
