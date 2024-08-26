package com.tranhuy105.admin.service.impl;

import com.tranhuy105.admin.dto.StateDTO;
import com.tranhuy105.admin.mapper.StateMapper;
import com.tranhuy105.admin.repository.StateRepository;
import com.tranhuy105.admin.service.StateService;
import com.tranhuy105.common.entity.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {
    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    @Override
    public List<StateDTO> findAll() {
        return stateRepository.findAll().stream().map(stateMapper::toDTO).toList();
    }

    @Override
    public List<StateDTO> findAllByCountry(Integer countryId) {
        if (countryId == null) {
            throw new IllegalArgumentException();
        }
        return stateRepository.findAllByCountryId(countryId).stream().map(stateMapper::toDTO).toList();
    }

    @Override
    public StateDTO findById(Integer id) {
        return stateMapper.toDTO(stateRepository.findById(id).orElse(null));
    }

    @Override
    public StateDTO save(StateDTO stateDTO) {
        if (stateDTO.getCountryId() == null) {
            throw new IllegalArgumentException("");
        }

        State state = stateMapper.toEntity(stateDTO);
        if (state == null || state.getName() == null || state.getName().isBlank() || state.getName().length() > 45) {
            throw new IllegalArgumentException("");
        }

        return stateMapper.toDTO(stateRepository.save(state));
    }

    @Override
    public void deleteById(Integer id) {
        stateRepository.deleteById(id);
    }
}
