package com.tranhuy105.site.repository;

import com.tranhuy105.site.dto.ProductOverview;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    @Test
    void testFindByAlias() {
        var foundProduct = productRepository.findByAlias("dell-xps-15");

        assertTrue(foundProduct.isPresent());
        assertEquals("dell-xps-15", foundProduct.get().getAlias());
        assertEquals("Dell XPS 15", foundProduct.get().getName());
    }

    @Test
    void testFindProductOverviewsByCategory() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer categoryId = 3;

        List<ProductOverview> productOverviews = productRepository.findProductOverviewsByCategory(categoryId, pageRequest);

        assertNotNull(productOverviews);
        assertFalse(productOverviews.isEmpty());

        ProductOverview productOverview = productOverviews.get(0);
        assertEquals(1, productOverview.getId());
        assertEquals("Dell XPS 15", productOverview.getName());
    }

    @Test
    void testGetBrandProduct() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer brandId = 4;

        List<ProductOverview> brandProducts = productRepository.getBrandProduct(brandId, pageRequest);

        assertNotNull(brandProducts);
        assertFalse(brandProducts.isEmpty());

        ProductOverview brandProduct = brandProducts.get(0);
        assertEquals(1, brandProduct.getId());
        assertEquals("Dell XPS 15", brandProduct.getName());
    }
}
