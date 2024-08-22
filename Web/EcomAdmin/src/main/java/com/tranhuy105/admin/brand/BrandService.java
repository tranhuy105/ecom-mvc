package com.tranhuy105.admin.brand;

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

    public List<Brand> findAllMin() {
        return brandRepository.findAllMin();
    }

    public Brand findById(Integer id) {
        return brandRepository.findById(id).orElse(null);
    }

    public void handleSaveLogo(Brand brand, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String fileName = FileUploadUtil.validateAndGetImageFilename(image);
            brand.setLogo(fileName);
            // if is creating new
            if (brand.getId() == null) {
                brand = brandRepository.save(brand);
            }
            String uploadDir = "brand_logos/" + brand.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, image);
        } else if (brand.getId() == null) {
            // if creating new without logo
            throw new IllegalArgumentException("Brand must have an logo");
        }
    }

    @Transactional
    public void save(Brand brand, MultipartFile file) throws IOException {
        validateBrand(brand);
        handleSaveLogo(brand, file);
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

    public void delete(Integer id) {
        brandRepository.delete(id);
    };
}
