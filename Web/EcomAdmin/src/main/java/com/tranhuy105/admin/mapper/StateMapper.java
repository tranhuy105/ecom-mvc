package com.tranhuy105.admin.mapper;

import com.tranhuy105.admin.dto.StateDTO;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.State;
import org.springframework.stereotype.Component;

@Component
public class StateMapper {
    public StateDTO toDTO(State state) {
        if (state == null) {
            return null;
        }
        StateDTO dto = new StateDTO();
        dto.setId(state.getId());
        dto.setName(state.getName());
        dto.setCountryId(state.getCountry() == null ? null : state.getCountry().getId());
        return dto;
    }

    public State toEntity(StateDTO dto) {
        if (dto == null) {
            return null;
        }
        Country country = new Country();
        country.setId(dto.getCountryId());
        return new State(dto.getId(), dto.getName(), country);
    }
}
