package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Brand;
import com.tranhuy105.common.entity.Category;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.site.dto.ProductOverview;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.BrandRepository;
import com.tranhuy105.site.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryService categoryService;

    private final Map<Integer, Brand> brandCache = new HashMap<>();
    private boolean isCacheLoaded = false;

    @Override
    public List<Brand> findAllAvailableProductBrand() {
        if (!isCacheLoaded) {
            loadCache();
        }
        return List.copyOf(brandCache.values());
    }

    @Override
    public List<ProductOverview> getRelatedProducts(Integer productId, PageRequest pageRequest) {
        return productRepository.findProductOverviewsByCategory(productId, pageRequest);
    }

    @Override
    public List<ProductOverview> getProductsByBrand(Integer brandId, PageRequest pageRequest) {
        return productRepository.getBrandProduct(brandId, pageRequest);
    }

    private synchronized void loadCache() {
        if (!isCacheLoaded) {
            List<Brand> brands = brandRepository.findAll().stream().sorted(Comparator.comparing(Brand::getName)).toList();
            for (Brand brand : brands) {
                brandCache.put(brand.getId(), brand);
            }
            isCacheLoaded = true;
        }
    }

    @Override
    public int pageSize() {
        return 20;
    }

    @Override
    public List<Product> findTopProductsByCategory(Category category) {
        if (category == null) {
            return new ArrayList<>();
        }

        List<Product> rawProduct = productRepository.findAllByCategory(
                Pageable.ofSize(pageSize()),
                category.getId()
        );

        if (rawProduct.isEmpty()) {
            return rawProduct;
        }

        return productRepository.findAllFull(rawProduct);
    }

    @Override
    public Product findByAlias(String alias) {
        return productRepository.findByAlias(alias)
                .orElseThrow(() -> new NotFoundException("product not found"));
    }

    @Override
    public Page<Product> findMany(String keyword, Integer categoryId, Integer brandId, BigDecimal minPrice, BigDecimal maxPrice,
                                  int page, String sort, String sortDirection) {
        Pageable pageable;
        try {
            if ("price".equalsIgnoreCase(sort)) {
                sort = "default_price";
            }
            pageable = PageRequest.of(page - 1, pageSize(), Sort.by(Sort.Direction.fromString(sortDirection), sort));
        } catch (Exception exception) {
            throw new IllegalArgumentException();
        }
        if (categoryId == null) {
            return productRepository.searchProduct(pageable, keyword, brandId, minPrice, maxPrice);
        } else {
            List<Integer> categoryIds = categoryService.getDescendent(categoryId);
            return productRepository.searchProductWithCategory(pageable, keyword, categoryIds, brandId, minPrice, maxPrice);
        }
    }

    @Override
    public List<Product> lazyFetchAttribute(List<Product> rawProducts) {
        if (rawProducts == null || rawProducts.isEmpty()) {
            return rawProducts;
        }
        return productRepository.findAllFull(rawProducts);
    }

    @Override
    public List<Product> lazyFetchAttribute(List<Product> rawProducts, String sort, String sortDirection) {
        List<Product> products = lazyFetchAttribute(rawProducts);

        if (sort != null && sortDirection != null && products != null &&  !products.isEmpty()) {
            Comparator<Product> comparator = getComparator(sort, sortDirection);
            products.sort(comparator);
        }

        return products;
    }

    private Comparator<Product> getComparator(String sort, String sortDirection) {
        Comparator<Product> comparator = switch (sort) {
            case "price" -> Comparator.comparing(Product::getDiscountedPrice);
            case "created_at" -> Comparator.comparing(Product::getCreatedAt);
            default -> Comparator.comparing(Product::getName);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}
