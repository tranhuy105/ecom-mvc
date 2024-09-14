package com.tranhuy105.site.service;


import com.tranhuy105.common.entity.Category;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAllRoot() {
        return categoryRepository.findAllRootCategory().stream().filter(Category::isEnabled).toList();
    }

    @Override
    public Category findByAlias(String alias) {
        return categoryRepository.findByAlias(alias).orElseThrow(
                () -> new NotFoundException("Category not found")
        );
    }

    @Override
    public List<Category> getBreadcrumbTrail(@NonNull Category category) {
        if (category.getId() == null) {
            return new ArrayList<>();
        }


        List<Category> parentTrails = categoryRepository.findCategoryPathById(category.getId());
        if (parentTrails.isEmpty()) {
            return parentTrails;
        }

        for (int i = 0; i < parentTrails.size() - 1; i++) {
            // set to parent to avoid lazy load n + 1 for each parent
            Category cat = parentTrails.get(i);
            Category parent = parentTrails.get(i+1);
            cat.setParent(parent);
        }

        Collections.reverse(parentTrails);
        return parentTrails;
    }

    @Override
    public List<Integer> getDescendent(@NonNull Integer categoryId) {
        return categoryRepository.findAllDescendent(categoryId);
    }
}
