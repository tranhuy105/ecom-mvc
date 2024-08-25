package com.tranhuy105.admin.service;

import com.tranhuy105.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BrandService {
    int getPageSize();

    Page<Brand> findAll(int page, String search);

    List<Brand> findAllMin();

    Brand findById(Integer id);

    @Transactional
    void save(Brand brand, MultipartFile file) throws IOException;

    void delete(Integer id);
}
