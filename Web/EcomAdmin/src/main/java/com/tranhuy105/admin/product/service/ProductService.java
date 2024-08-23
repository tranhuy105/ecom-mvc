package com.tranhuy105.admin.product.service;

import com.tranhuy105.admin.product.repository.ProductRepository;
import com.tranhuy105.common.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    public static final int PAGE_SIZE = 4;
    private final ProductRepository productRepository;

    @Override
    public Page<Product> findAll(int page, String search, Integer category) {
        return productRepository.findAll(PageRequest.of(page-1, PAGE_SIZE), search, category);
    }

    @Override
    public List<Product> lazyFetchAttribute(List<Product> products) {
        return productRepository.findAllFull(products);
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findByIdFull(id).orElse(null);
    }

    @Override
    public void save(Product product, MultipartFile file) throws IOException {

    }

    @Override
    public void delete(Integer id) {

    }
}
