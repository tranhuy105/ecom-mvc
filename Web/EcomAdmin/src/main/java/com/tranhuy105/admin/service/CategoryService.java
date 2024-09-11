package com.tranhuy105.admin.service;

import com.tranhuy105.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CategoryService {
    int getPageSize();

    Page<Category> findAll(Integer page, String search);

    Category findById(Integer id);

    @Transactional
    Category save(Category newCategory);

    List<Category> findAllWithHierarchy();

    List<Category> findAllById(List<Integer> ids);

    void updateCategoryInCache(Category newCategory);

    boolean delete(Integer id);
}
