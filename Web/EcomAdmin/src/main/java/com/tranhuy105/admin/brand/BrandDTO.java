package com.tranhuy105.admin.brand;

import com.tranhuy105.admin.category.CategoryDTO;
import com.tranhuy105.common.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BrandDTO {
    private Integer id;
    private String name;
    private String logo;
    private List<CategoryDTO> categories;

    public BrandDTO(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.logo = brand.getImagePath();
        this.categories = brand.getCategories().stream()
                .map(cat -> new CategoryDTO(cat.getId(), cat.getName()))
                .toList();
    }
}
