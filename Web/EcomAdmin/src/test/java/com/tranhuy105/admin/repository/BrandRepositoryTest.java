package com.tranhuy105.admin.repository;

import com.tranhuy105.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository repository;

    @Test
    public void testFindAllWithFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Brand> page = repository.findAllWithFilter(pageable, "Acer");

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent()).extracting(Brand::getName)
                .contains("Acer");
    }

    @Test
    public void testFindWithCategories() {
        Brand existingBrand = repository.findById(4).orElseThrow();
        List<Brand> brands = repository.findWithCategories(List.of(existingBrand));
        assertThat(brands).hasSize(1);
        assertThat(brands.get(0).getName()).isEqualTo("Dell");
    }

    @Test
    public void testFindByName() {
        Optional<Integer> id = repository.findByName("HP");
        assertThat(id).isPresent();
    }

    @Test
    public void testFindById() {
        Optional<Brand> foundBrand = repository.findById(5);
        assertThat(foundBrand).isPresent();
        assertThat(foundBrand.get().getName()).isEqualTo("HP");
    }

    @Test
    public void testDelete() {
        Brand existingBrand = repository.findById(5).orElseThrow();
        repository.delete(existingBrand.getId());
        Optional<Brand> deletedBrand = repository.findById(5);
        assertThat(deletedBrand).isNotPresent();
    }

    @Test
    public void testFindAllMin() {
        List<Brand> brands = repository.findAllMin();
        assertThat(brands).hasSizeGreaterThan(0);
        assertThat(brands).extracting(Brand::getName)
                .contains("Acer", "Apple", "Samsung", "Dell", "HP", "Lenovo", "Sony", "LG", "Microsoft", "NVIDIA", "Intel", "Corsair", "Asus", "MSI", "Razer");
    }
}
