package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Category;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CategoryService {
    List<Category> findAllRoot();

    Category findByAlias(String alias);

    List<Category> getBreadcrumbTrail(@NonNull Category category);
}
