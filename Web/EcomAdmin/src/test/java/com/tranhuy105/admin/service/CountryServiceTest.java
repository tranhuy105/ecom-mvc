package com.tranhuy105.admin.service;

import com.tranhuy105.admin.repository.CountryRepository;
import com.tranhuy105.admin.service.impl.CountryServiceImpl;
import com.tranhuy105.common.entity.Country;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImpl countryService;

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
    public void findAllTest() {
        List<Country> countries = Arrays.asList(
                new Country(1, "Country A", "CA", null),
                new Country(2, "Country B", "CB", null)
        );

        when(countryRepository.findAllOrderByName()).thenReturn(countries);

        List<Country> result = countryService.findAll();
        assertEquals(2, result.size());
        assertEquals("Country A", result.get(0).getName());
        assertEquals("Country B", result.get(1).getName());
    }

    @Test
    public void saveTest() {
        Country country = new Country(1, "Country A", "CA", null);

        when(countryRepository.save(any(Country.class))).thenReturn(country);

        Country result = countryService.save(country);
        assertEquals("Country A", result.getName());
        assertEquals("CA", result.getCode());
        verify(countryRepository, times(1)).save(country);
    }
}