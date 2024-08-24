package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Category;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.site.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;

    @Override
    public List<Product> findTopProductsByCategory(Category category) {
        if (category == null) {
            return new ArrayList<>();
        }

        List<Product> rawProduct = productRepository.findAllByCategory(
                Pageable.ofSize(50),
                category.getId()
        );

        if (rawProduct.isEmpty()) {
            return rawProduct;
        }

        return productRepository.findAllFull(rawProduct);
    }
}
