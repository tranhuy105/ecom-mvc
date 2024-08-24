package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindCategoryPathByAlias() {
        List<Category> breadcrumbTrail = categoryRepository.findCategoryPathById(5);

        assertThat(breadcrumbTrail).isNotNull();
        assertThat(breadcrumbTrail.size()).isGreaterThanOrEqualTo(3);

        for (Category category : breadcrumbTrail) {
            System.out.println(category.getName() + " -> " + category.getAlias());
        }
    }
}
