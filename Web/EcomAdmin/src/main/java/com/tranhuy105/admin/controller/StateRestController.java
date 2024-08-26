package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.dto.StateDTO;
import com.tranhuy105.admin.service.StateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StateRestController {
    private final StateService service;

    @GetMapping("countries/{countryId}/states")
    public ResponseEntity<List<StateDTO>> findAll(@PathVariable Integer countryId) {
        return ResponseEntity.ok(service.findAllByCountry(countryId));
    }

    @PostMapping("/states")
    public ResponseEntity<StateDTO> saveState(
            @RequestBody StateDTO stateDTO
    ) {
        StateDTO dto;
        try {
            dto = service.save(stateDTO);
        } catch (IllegalArgumentException exception) {
            log.info(exception.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException exception) {
            log.error("UNEXPECTED EXCEPTION OCCURRED WHILE SAVING STATE", exception);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/states/{id}")
    public ResponseEntity<Void> deleteState(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
