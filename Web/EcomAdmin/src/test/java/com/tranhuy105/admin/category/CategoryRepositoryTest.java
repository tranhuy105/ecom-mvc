package com.tranhuy105.admin.category;

import com.tranhuy105.admin.repository.CategoryRepository;
import com.tranhuy105.common.entity.Category;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testGetCategoryFull() {
        Category category = categoryRepository.findByIdWithChildrenAndParent(4).orElse(null);

        assertThat(category).isNotNull();
        assertThat(category.getChildren()).isNotEmpty();
        assertThat(category.getParent()).isNotNull();
    }

    @Test
    public void testGetCategoryWithChild() {
        Category category = categoryRepository.findByIdWithChildren(1).orElse(null);

        assertThat(category).isNotNull();
        assertThat(category.getChildren()).isNotEmpty();
    }

    @Test
    public void testCreateCategory() {
        // Arrange: Create a new Category object for the parent
        Category parentCategory = new Category();
        parentCategory.setName("Parent Category");
        parentCategory.setAlias("parent");
        parentCategory.setImage("parent-image.png");
        parentCategory.setEnabled(true);

        // Create a new Category object for the child
        Category newCategory = new Category();
        newCategory.setName("New Category");
        newCategory.setAlias("new");
        newCategory.setImage("new-image.png");
        newCategory.setEnabled(true);

        parentCategory.addChild(newCategory);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        // Assert: Verify the parent category was saved correctly
        assertThat(savedParentCategory).isNotNull();
        assertThat(savedParentCategory.getId()).isNotNull();
        assertThat(savedParentCategory.getName()).isEqualTo("Parent Category");
        assertThat(savedParentCategory.getAlias()).isEqualTo("parent");
        assertThat(savedParentCategory.getImage()).isEqualTo("parent-image.png");
        assertThat(savedParentCategory.isEnabled()).isTrue();
        assertThat(savedParentCategory.getChildren()).hasSize(1); // Ensure the child is saved

        Category savedChildCategory = savedParentCategory.getChildren().iterator().next();

        // Assert: Verify the child category was saved correctly
        assertThat(savedChildCategory).isNotNull();
        assertThat(savedChildCategory.getId()).isNotNull();
        assertThat(savedChildCategory.getName()).isEqualTo("New Category");
        assertThat(savedChildCategory.getAlias()).isEqualTo("new");
        assertThat(savedChildCategory.getImage()).isEqualTo("new-image.png");
        assertThat(savedChildCategory.isEnabled()).isTrue();
        assertThat(savedChildCategory.getParent()).isEqualTo(savedParentCategory);
    }

    @Test
    public void testDeleteParentCategoryCascadesToChildren() {
        // Arrange: Create and save a parent category with children
        Category parentCategory = new Category();
        parentCategory.setName("Parent Category");
        parentCategory.setAlias("parent");
        parentCategory.setImage("parent-image.png");
        parentCategory.setEnabled(true);

        Category childCategory1 = new Category();
        childCategory1.setName("Child Category 1");
        childCategory1.setAlias("child1");
        childCategory1.setImage("child1-image.png");
        childCategory1.setEnabled(true);

        Category childCategory2 = new Category();
        childCategory2.setName("Child Category 2");
        childCategory2.setAlias("child2");
        childCategory2.setImage("child2-image.png");
        childCategory2.setEnabled(true);

        parentCategory.addChild(childCategory1);
        parentCategory.addChild(childCategory2);

        categoryRepository.save(parentCategory);

        // Act: Delete the parent category
        categoryRepository.delete(parentCategory);

        // Assert: Verify the parent and its children are deleted
        assertThat(categoryRepository.findById(parentCategory.getId())).isNotPresent();

        for (Category child : parentCategory.getChildren()) {
            assertThat(categoryRepository.findById(child.getId())).isNotPresent();
        }
    }
}