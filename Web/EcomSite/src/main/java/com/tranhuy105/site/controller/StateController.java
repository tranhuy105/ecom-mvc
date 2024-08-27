package com.tranhuy105.site.controller;

import com.tranhuy105.site.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StateController {
    private final StateRepository stateRepository;

    @GetMapping("/countries/{countryId}/states")
    public ResponseEntity<List<String>> findAllState(@PathVariable Integer countryId) {
        return ResponseEntity.ok(stateRepository.findAllByCountry(countryId));
    }
}
