package com.tranhuy105.admin.repository;

import com.tranhuy105.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    public void testFindByIdWithChildren() {
        Optional<Category> category = repository.findByIdWithChildren(1);

        assertThat(category).isPresent();
        assertThat(category.get().getName()).isEqualTo("Computers");
        assertThat(category.get().getChildren()).hasSize(3);
    }

    @Test
    public void testFindByIdWithChildrenAndParent() {
        Optional<Category> category = repository.findByIdWithChildrenAndParent(4);

        assertThat(category).isPresent();
        assertThat(category.get().getName()).isEqualTo("Computer Components");
        assertThat(category.get().getParent()).isNotNull();
        assertThat(category.get().getChildren()).hasSize(13);
    }

    @Test
    public void testFindAllWithChildrenAndParent() {
        List<Category> categories = repository.findAllWithChildrenAndParent();

        assertThat(categories).isNotEmpty();
        assertThat(categories).extracting(Category::getName)
                .contains("Computers", "Desktops", "Laptops", "Computer Components");
    }

    @Test
    public void testFindByNameOrAlias() {
        List<Integer> categories = repository.findByNameOrAlias("RAM", "cpu");

        assertThat(categories).isNotEmpty();
        assertThat(categories)
                .containsAll(List.of(5,6));
    }

    @Test
    public void testDelete() {
        repository.delete(1);

        Optional<Category> deletedCategory = repository.findById(1);
        assertThat(deletedCategory).isNotPresent();
    }
}
