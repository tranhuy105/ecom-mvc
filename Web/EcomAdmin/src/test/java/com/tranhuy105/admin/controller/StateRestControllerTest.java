package com.tranhuy105.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranhuy105.admin.dto.StateDTO;
import com.tranhuy105.admin.service.StateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StateRestController.class)
@ActiveProfiles("test")
class StateRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StateService stateService;

    @Autowired
    private ObjectMapper objectMapper;

    private StateDTO stateDTO;

    @BeforeEach
    void setUp() {
        stateDTO = new StateDTO();
        stateDTO.setId(1);
        stateDTO.setName("California");
        stateDTO.setCountryId(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindAll() throws Exception {
        when(stateService.findAllByCountry(1)).thenReturn(List.of(stateDTO));

        mockMvc.perform(get("/countries/{id}/states", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("California"));

        verify(stateService, times(1)).findAllByCountry(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveState_Create() throws Exception {
        StateDTO newStateDTO = new StateDTO();
        newStateDTO.setName("New York");
        newStateDTO.setCountryId(1);

        when(stateService.save(any(StateDTO.class))).thenReturn(stateDTO);

        mockMvc.perform(post("/states")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("California"));

        verify(stateService, times(1)).save(any(StateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveState_Update() throws Exception {
        stateDTO.setName("Updated California");

        when(stateService.save(any(StateDTO.class))).thenReturn(stateDTO);

        mockMvc.perform(post("/states")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated California"));

        verify(stateService, times(1)).save(any(StateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveState_Invalid() throws Exception {
        StateDTO invalidStateDTO = new StateDTO();

        doThrow(new IllegalArgumentException()).when(stateService).save(any(StateDTO.class));

        mockMvc.perform(post("/states")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStateDTO)))
                .andExpect(status().isBadRequest());

        verify(stateService, times(1)).save(any(StateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteState() throws Exception {
        doNothing().when(stateService).deleteById(anyInt());

        mockMvc.perform(delete("/states/{id}", 1)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(stateService, times(1)).deleteById(anyInt());
    }
}
