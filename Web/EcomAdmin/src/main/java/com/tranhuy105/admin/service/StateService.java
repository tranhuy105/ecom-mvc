package com.tranhuy105.admin.service;

import com.tranhuy105.admin.dto.StateDTO;

import java.util.List;

public interface StateService {
    List<StateDTO> findAll();

    List<StateDTO> findAllByCountry(Integer countryId);

    StateDTO findById(Integer id);

    StateDTO save(StateDTO stateDTO);

    void deleteById(Integer id);
}
