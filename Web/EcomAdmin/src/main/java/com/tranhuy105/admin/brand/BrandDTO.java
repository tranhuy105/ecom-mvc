package com.tranhuy105.admin.brand;

import com.tranhuy105.admin.category.CategoryDTO;
import com.tranhuy105.admin.category.CategoryService;
import com.tranhuy105.common.entity.Brand;
import com.tranhuy105.common.entity.Category;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BrandDTO {
    private Integer id;
    private String name;
    private String logo;
    private String logoPath = "/images/default_user.jpg";
    private List<CategoryDTO> categories = new ArrayList<>();
    private List<Integer> categoryIds = new ArrayList<>();

    public BrandDTO(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.logoPath = brand.getImagePath();
        this.logo = brand.getLogo();
        this.categories = brand.getCategories().stream()
                .map(cat -> new CategoryDTO(cat.getId(), cat.getName()))
                .toList();
        this.categoryIds = this.categories.stream().map(CategoryDTO::getId).toList();
    }

    public Brand toBrand(CategoryService categoryService) {
        List<Category> validCategories = categoryService.findAllById(categoryIds);
        if (validCategories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("Invalid category present");
        }

        Brand brand = new Brand();
        brand.setId(this.id);
        brand.setName(this.name);
        brand.setLogo(this.logo);
        brand.setCategories(validCategories);
        return brand;
    }
}
