package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Brand;
import com.tranhuy105.common.entity.Category;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.site.dto.ProductOverview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    int pageSize();

    List<Product> findTopProductsByCategory(Category category);

    Product findByAlias(String alias);

    Page<Integer> findMany(String keyword,
                           Integer categoryId,
                           Integer brandId,
                           BigDecimal minPrice,
                           BigDecimal maxPrice,
                           int page,
                           String sort,
                           String sortDirection);

    List<Product> lazyFetchAttribute(List<Integer> rawProducts);

    List<Product> lazyFetchAttribute(List<Integer> rawProducts, String sort, String sortDirection);

    List<Brand> findAllAvailableProductBrand();

    List<ProductOverview> getRelatedProducts(Integer productId, PageRequest of);

    List<ProductOverview> getProductsByBrand(Integer brandId, PageRequest of);
}
