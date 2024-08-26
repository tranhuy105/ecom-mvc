package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.service.CountryService;
import com.tranhuy105.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryRestController.class)
class CountryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindAllCountry() throws Exception {
        List<Country> countries = Arrays.asList(
                new Country(1, "Country1", "CODE"),
                new Country(2, "Country2", "COD")
        );

        when(countryService.findAll()).thenReturn(countries);

        mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Country1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Country2"));

        verify(countryService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveCountry_Valid() throws Exception {
        Country country = new Country(1, "ValidCountry", "CODE");

        when(countryService.save(any(Country.class))).thenReturn(country);

        mockMvc.perform(post("/countries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"ValidCountry\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ValidCountry"));

        verify(countryService, times(1)).save(any(Country.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveCountry_Invalid() throws Exception {
        doThrow(new IllegalArgumentException()).when(countryService).save(any(Country.class));

        mockMvc.perform(post("/countries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"InvalidCountry\"}"))
                .andExpect(status().isBadRequest());

        verify(countryService, times(1)).save(any(Country.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveCountry_UnexpectedError() throws Exception {
        doThrow(new RuntimeException("Unexpected error")).when(countryService).save(any(Country.class));

        mockMvc.perform(post("/countries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"ValidCountry\"}"))
                .andExpect(status().isInternalServerError());

        verify(countryService, times(1)).save(any(Country.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCountry() throws Exception {
        doNothing().when(countryService).delete(anyInt());

        mockMvc.perform(delete("/countries/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(countryService, times(1)).delete(1);
    }
}
