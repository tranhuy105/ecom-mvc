package com.tranhuy105.admin.service.impl;

import com.tranhuy105.admin.repository.BrandRepository;
import com.tranhuy105.admin.service.BrandService;
import com.tranhuy105.admin.utils.FileUploadUtil;
import com.tranhuy105.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    public static final int PAGE_SIZE = 15;
    private final BrandRepository brandRepository;

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public Page<Brand> findAll(int page, String search) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        Page<Brand> brandPage;
        if (search == null || search.isBlank()) {
            brandPage = brandRepository.findAll(pageable);
        } else {
            brandPage = brandRepository.findAllWithFilter(pageable, search);
        }
        List<Brand> brandsWithCategories = brandRepository.findWithCategories(brandPage.getContent());
        return new PageImpl<>(brandsWithCategories, pageable, brandPage.getTotalElements());
    }

    @Override
    public List<Brand> findAllMin() {
        return brandRepository.findAllMin();
    }

    @Override
    public Brand findById(Integer id) {
        return brandRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void save(Brand brand, MultipartFile file) throws IOException {
        validateBrand(brand);
        brandRepository.save(brand);
    }

    private void validateBrand(Brand brand) {
        if (brand.getName()== null || brand.getName().isBlank()) {
            throw new IllegalArgumentException("Invalid brand name.");
        }

        Optional<Integer> brandDB = brandRepository.findByName(brand.getName());
        if (brandDB.isPresent()) {
            if (brand.getId() == null) {
                throw new IllegalArgumentException("Brand Name Already Exists.");
            }

            if (!brand.getId().equals(brandDB.get())) {
                throw new IllegalArgumentException("Brand Name Already Exists.");
            }
        }
    }

    @Override
    public void delete(Integer id) {
        brandRepository.delete(id);
    };
}
