package com.tranhuy105.admin.service;

import com.tranhuy105.admin.repository.BrandRepository;
import com.tranhuy105.admin.service.impl.BrandServiceImpl;
import com.tranhuy105.admin.utils.FileUploadUtil;
import com.tranhuy105.common.entity.Brand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class BrandServiceTest {
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    void testGetPageSize() {
        assertEquals(15, brandService.getPageSize());
    }

    @Test
    void testFindAllWithSearch() {
        Pageable pageable = PageRequest.of(0, 15);
        List<Brand> brands = List.of(new Brand(), new Brand());
        Page<Brand> brandPage = new PageImpl<>(brands, pageable, brands.size());

        when(brandRepository.findAllWithFilter(pageable, "search")).thenReturn(brandPage);
        when(brandRepository.findWithCategories(brands)).thenReturn(brands);

        Page<Brand> result = brandService.findAll(1, "search");

        assertEquals(brands.size(), result.getContent().size());
        verify(brandRepository).findAllWithFilter(pageable, "search");
    }

    @Test
    void testFindAllWithoutSearch() {
        Pageable pageable = PageRequest.of(0, 15);
        List<Brand> brands = List.of(new Brand(), new Brand());
        Page<Brand> brandPage = new PageImpl<>(brands, pageable, brands.size());

        when(brandRepository.findAll(pageable)).thenReturn(brandPage);
        when(brandRepository.findWithCategories(brands)).thenReturn(brands);

        Page<Brand> result = brandService.findAll(1, null);

        assertEquals(brands.size(), result.getContent().size());
        verify(brandRepository).findAll(pageable);
    }

    @Test
    void testFindAllMin() {
        List<Brand> brands = List.of(new Brand(), new Brand());

        when(brandRepository.findAllMin()).thenReturn(brands);

        List<Brand> result = brandService.findAllMin();

        assertEquals(brands.size(), result.size());
        verify(brandRepository).findAllMin();
    }

    @Test
    void testFindById() {
        Brand brand = new Brand();
        brand.setId(1);

        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));

        Brand result = brandService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(brandRepository).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        when(brandRepository.findById(1)).thenReturn(Optional.empty());

        Brand result = brandService.findById(1);

        assertNull(result);
        verify(brandRepository).findById(1);
    }

    @Test
    void testSaveWithLogo(){
        Brand brand = new Brand();
        brand.setName("test brand");
        brand.setDescription("description");
        brand.setLogo("logo.png");
        brandService.save(brand);
        verify(brandRepository).save(brand);
    }

    @Test
    void testSaveWithoutLogo() {
        Brand brand = new Brand();
        brand.setName("test brand");
        brand.setDescription("description");
        brand.setId(null);
        brand.setLogo(null);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> brandService.save(brand),
                "Expected save() to throw, but it didn't"
        );

        assertEquals("Brand must have an logo", thrown.getMessage());
        verify(brandRepository, never()).save(brand);
    }

    @Test
    void testDelete() {
        brandService.delete(1);
        verify(brandRepository).delete(1);
    }
}
