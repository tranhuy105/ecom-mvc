package com.tranhuy105.admin.service;

import com.tranhuy105.admin.dto.StateDTO;
import com.tranhuy105.admin.mapper.StateMapper;
import com.tranhuy105.admin.repository.StateRepository;
import com.tranhuy105.admin.service.impl.StateServiceImpl;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class StateServiceTest {
    @Mock
    private StateRepository stateRepository;

    @Mock
    private StateMapper stateMapper;

    @InjectMocks
    private StateServiceImpl stateService;

    private State state;
    private StateDTO stateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new State();
        state.setId(1);
        state.setName("California");
        Country country = new Country(1, "America", "USA");
        state.setCountry(country);

        stateDTO = new StateDTO();
        stateDTO.setId(1);
        stateDTO.setName("California");
        stateDTO.setCountryId(1);
    }

    @Test
    void testFindAll() {
        when(stateRepository.findAll()).thenReturn(List.of(state));
        when(stateMapper.toDTO(any(State.class))).thenReturn(stateDTO);

        List<StateDTO> result = stateService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("California", result.get(0).getName());

        verify(stateRepository, times(1)).findAll();
        verify(stateMapper, times(1)).toDTO(any(State.class));
    }

    @Test
    void testFindAllByCountry() {
        when(stateRepository.findAllByCountryId(anyInt())).thenReturn(List.of(state));
        when(stateMapper.toDTO(any(State.class))).thenReturn(stateDTO);

        List<StateDTO> result = stateService.findAllByCountry(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("California", result.get(0).getName());

        verify(stateRepository, times(1)).findAllByCountryId(anyInt());
        verify(stateMapper, times(1)).toDTO(any(State.class));
    }

    @Test
    void testFindAllByCountryWithNullIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> stateService.findAllByCountry(null));
    }

    @Test
    void testFindById() {
        when(stateRepository.findById(anyInt())).thenReturn(Optional.of(state));
        when(stateMapper.toDTO(any(State.class))).thenReturn(stateDTO);

        StateDTO result = stateService.findById(1);

        assertNotNull(result);
        assertEquals("California", result.getName());

        verify(stateRepository, times(1)).findById(anyInt());
        verify(stateMapper, times(1)).toDTO(any(State.class));
    }

    @Test
    void testSave() {
        when(stateMapper.toEntity(any(StateDTO.class))).thenReturn(state);
        when(stateRepository.save(any(State.class))).thenReturn(state);
        when(stateMapper.toDTO(any(State.class))).thenReturn(stateDTO);

        StateDTO result = stateService.save(stateDTO);

        assertNotNull(result);
        assertEquals("California", result.getName());

        verify(stateMapper, times(1)).toEntity(any(StateDTO.class));
        verify(stateRepository, times(1)).save(any(State.class));
        verify(stateMapper, times(1)).toDTO(any(State.class));
    }

    @Test
    void testSaveWithNullCountryIdThrowsException() {
        stateDTO.setCountryId(null);
        assertThrows(IllegalArgumentException.class, () -> stateService.save(stateDTO));
    }

    @Test
    void testSaveWithInvalidNameThrowsException() {
        stateDTO.setName(null);
        assertThrows(IllegalArgumentException.class, () -> stateService.save(stateDTO));

        stateDTO.setName("");
        assertThrows(IllegalArgumentException.class, () -> stateService.save(stateDTO));

        stateDTO.setName("a".repeat(46)); // 46 characters long
        assertThrows(IllegalArgumentException.class, () -> stateService.save(stateDTO));
    }

    @Test
    void testDeleteById() {
        doNothing().when(stateRepository).deleteById(anyInt());

        stateService.deleteById(1);

        verify(stateRepository, times(1)).deleteById(anyInt());
    }
}
