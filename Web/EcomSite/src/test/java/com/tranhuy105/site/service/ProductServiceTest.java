package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Category;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ProductServiceTest {
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    private List<Product> products;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Product1");
        product1.setPrice(BigDecimal.valueOf(20));
        product1.setCreatedAt(LocalDateTime.now());

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Product2");
        product2.setPrice(BigDecimal.valueOf(10));
        product2.setCreatedAt(LocalDateTime.now());

        Product product3 = new Product();
        product3.setId(3);
        product3.setName("Product3");
        product3.setPrice(BigDecimal.valueOf(5));
        product3.setCreatedAt(LocalDateTime.now());

        products.add(product1);
        products.add(product2);
        products.add(product3);
    }

    @Test
    void testPageSize() {
        assertEquals(20, productService.pageSize());
    }

    @Test
    void testFindTopProductsByCategory() {
        Category category = new Category();
        category.setId(1);

        List<Integer> rawProductIds = Arrays.asList(1, 2, 3);

        when(productRepository.findAllByCategory(any(Pageable.class), eq(category.getId())))
                .thenReturn(rawProductIds);
        when(productRepository.findAllFull(rawProductIds))
                .thenReturn(products);

        List<Product> result = productService.findTopProductsByCategory(category);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Product1", result.get(0).getName());
        assertEquals("Product2", result.get(1).getName());
        assertEquals("Product3", result.get(2).getName());
    }

    @Test
    void testFindTopProductsByCategory_CategoryNull() {
        List<Product> result = productService.findTopProductsByCategory(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByAlias() {
        Product product = new Product();
        String alias = "test-alias";
        product.setAlias(alias);

        when(productRepository.findByAlias(alias)).thenReturn(Optional.of(product));

        Product result = productService.findByAlias(alias);
        assertNotNull(result);
        assertEquals(alias, result.getAlias());
    }

    @Test
    void testFindByAlias_NotFound() {
        String alias = "test-alias";

        when(productRepository.findByAlias(alias)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> productService.findByAlias(alias));
        assertEquals("product not found", thrown.getMessage());
    }

    @Test
    void testFindMany() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "default_price"));
        Page<Integer> page = mock(Page.class);

        when(productRepository.searchProduct(any(Pageable.class), anyString(), any(), any(), any()))
                .thenReturn(page);

        Page<Integer> result = productService.findMany("keyword", null, null, BigDecimal.ZERO, BigDecimal.TEN, 1, "price", "asc");

        assertNotNull(result);
        assertEquals(page, result);
        verify(productRepository).searchProduct(any(Pageable.class), eq("keyword"), any(), any(), any());
    }

    @Test
    void testFindMany_WithCategory() {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "default_price"));
        Page<Integer> page = mock(Page.class);

        Category category = new Category();
        category.setId(1);
        List<Integer> categoryIds = Arrays.asList(1, 2, 3);

        when(categoryService.getDescendent(category.getId())).thenReturn(categoryIds);
        when(productRepository.searchProductWithCategory(any(Pageable.class), anyString(), any(), any(), any(), any()))
                .thenReturn(page);

        Page<Integer> result = productService.findMany("keyword", 1, null, BigDecimal.ZERO, BigDecimal.TEN, 1, "price", "asc");

        assertNotNull(result);
        assertEquals(page, result);
        verify(productRepository).searchProductWithCategory(any(Pageable.class), eq("keyword"), eq(categoryIds), any(), any(), any());
    }

    @Test
    void testLazyFetchAttribute() {
        List<Integer> rawProductIds = Arrays.asList(1, 2, 3);

        when(productRepository.findAllFull(rawProductIds)).thenReturn(products);

        List<Product> result = productService.lazyFetchAttribute(rawProductIds);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Product1", result.get(0).getName());
    }

    @Test
    void testLazyFetchAttribute_WithSorting() {
        List<Integer> rawProductIds = Arrays.asList(1, 2, 3);
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Product1");
        product1.setPrice(BigDecimal.valueOf(20));

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Product2");
        product2.setPrice(BigDecimal.valueOf(10));

        Product product3 = new Product();
        product3.setId(3);
        product3.setName("Product3");
        product3.setPrice(BigDecimal.valueOf(5));

        List<Product> products = Arrays.asList(product1, product2, product3);

        when(productRepository.findAllFull(rawProductIds)).thenReturn(products);

        List<Product> result = productService.lazyFetchAttribute(rawProductIds, "price", "asc");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Product3", result.get(0).getName());
        assertEquals("Product2", result.get(1).getName());
        assertEquals("Product1", result.get(2).getName());

    }
}