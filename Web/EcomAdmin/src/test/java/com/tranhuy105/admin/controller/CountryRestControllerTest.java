package com.tranhuy105.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranhuy105.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CountryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findAllCountriesTest() throws Exception {
        String url = "/countries";
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$", is(not(empty()))))
//                .andExpect(jsonPath("$[0].id", is(notNullValue())))
//                .andExpect(jsonPath("$[0].name", is(notNullValue())))
//                .andExpect(jsonPath("$[0].code", is(notNullValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void saveCountryTest() throws Exception {
        String url = "/countries";
        Country country = new Country();
        country.setName("Test Country");
        country.setCode("TC");

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.name", is("Test Country")))
                .andExpect(jsonPath("$.code", is("TC")));
    }
}
