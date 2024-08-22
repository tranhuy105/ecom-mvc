package com.tranhuy105.admin.product.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.tranhuy105.common.entity.Product;
import com.tranhuy105.common.entity.ProductDetail;
import com.tranhuy105.common.entity.ProductImage;
import com.tranhuy105.common.entity.Sku;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindProductWithDetailsImagesAndSkus() {
        Optional<Product> productOpt = productRepository.findByIdFull(1);
        assertTrue(productOpt.isPresent(), "Product should be found");

        Product product = productOpt.get();
        assertEquals("Dell XPS 15", product.getName(), "Product name should match");

        // Assert product details
        Set<ProductDetail> details = product.getAdditionalDetails();
        assertEquals(4, details.size(), "There should be 4 product details");
        assertTrue(details.stream().anyMatch(d -> "Processor".equals(d.getName()) && "Intel Core i7".equals(d.getValue())), "Processor detail should match");
        assertTrue(details.stream().anyMatch(d -> "RAM".equals(d.getName()) && "16GB".equals(d.getValue())), "RAM detail should match");
        assertTrue(details.stream().anyMatch(d -> "Storage".equals(d.getName()) && "512GB SSD".equals(d.getValue())), "Storage detail should match");
        assertTrue(details.stream().anyMatch(d -> "Display".equals(d.getName()) && "15-inch 4K".equals(d.getValue())), "Display detail should match");

        // Assert product images
        Set<ProductImage> images = product.getImages();
        assertEquals(2, images.size(), "There should be 2 product images");
        assertTrue(images.stream().anyMatch(i -> "dell-xps-15-front.jpg".equals(i.getName())), "Front image should be present");
        assertTrue(images.stream().anyMatch(i -> "dell-xps-15-side.jpg".equals(i.getName())), "Side image should be present");

        // Assert SKUs
        Set<Sku> skus = product.getSkus();
        assertEquals(2, skus.size(), "There should be 2 SKUs");
        assertTrue(skus.stream().anyMatch(s -> "DELL-XPS-15-1".equals(s.getSkuCode())), "SKU code DELL-XPS-15-1 should be present");
        assertTrue(skus.stream().anyMatch(s -> "DELL-XPS-15-2".equals(s.getSkuCode())), "SKU code DELL-XPS-15-2 should be present");
    }

    @Test
    void testFindProductWithNonExistentAlias() {
        Optional<Product> productOpt = productRepository.findById(-3);
        assertFalse(productOpt.isPresent(), "Product should not be found");
    }

    @Test
    void testFindAllFullWithPagination() {
        // Fetch the first 2 products using pagination
        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> productPage = productRepository.findAll(pageable);

        // Ensure that exactly 2 products are returned
        assertEquals(2, productPage.getContent().size(), "There should be 2 products returned");

        // Extract the products
        Product product1 = productPage.getContent().get(0);
        Product product2 = productPage.getContent().get(1);

        // Verify the first product's details
        assertEquals("Dell XPS 15", product1.getName(), "Product name should match");
        assertEquals(2, product1.getImages().size(), "There should be 2 product images for the first product");
        assertEquals("Dell", product1.getBrand().getName(), "Brand name should match");
        assertEquals(4, product1.getBrand().getId(), "Brand ID should match");
        assertEquals("Laptops", product1.getCategory().getName(), "Category name should match");
        assertEquals(3, product1.getCategory().getId(), "Category ID should match");

        // Verify the second product's details
        assertEquals("Apple MacBook Air", product2.getName(), "Product name should match");
        assertEquals(2, product2.getImages().size(), "There should be 2 product images for the second product");
        assertEquals("Apple", product2.getBrand().getName(), "Brand name should match");
        assertEquals(2, product2.getBrand().getId(), "Brand ID should match");
        assertEquals("Laptops", product2.getCategory().getName(), "Category name should match");
        assertEquals(3, product2.getCategory().getId(), "Category ID should match");
    }
}