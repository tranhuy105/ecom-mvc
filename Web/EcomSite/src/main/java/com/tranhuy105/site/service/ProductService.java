package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Category;
import com.tranhuy105.common.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findTopProductsByCategory(Category category);

    Product findByAlias(String alias);
}
