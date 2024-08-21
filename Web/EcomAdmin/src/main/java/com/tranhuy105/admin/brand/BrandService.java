package com.tranhuy105.admin.brand;

import com.tranhuy105.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    public static final int PAGE_SIZE = 15;
    private final BrandRepository brandRepository;

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
}
