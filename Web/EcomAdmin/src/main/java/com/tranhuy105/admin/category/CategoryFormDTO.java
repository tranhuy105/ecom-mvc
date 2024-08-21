package com.tranhuy105.admin.category;

import com.tranhuy105.common.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CategoryFormDTO {
    private Integer id;
    private String name;
    private String alias;
    private String image;
    private boolean enabled;
    private Integer parentId;

    public CategoryFormDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.alias = category.getAlias();
        this.image = category.getImage();
        this.enabled = category.isEnabled();
        this.parentId = category.getParent() == null ? null : category.getParent().getId();
    }

    public Category toCategory(CategoryService categoryService) {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        category.setAlias(this.alias);
        category.setImage(this.image);
        category.setEnabled(this.enabled);

        if (this.parentId != null && this.parentId != 0) {
            Category parent = categoryService.findById(this.parentId);
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return category;
    }


    public String getImagePath() {
        if (this.id == null || this.image == null) {
            return "/images/default_user.jpg";
        }
        return String.format("/category-images/%s/%s", this.id, this.image);
    }
}
