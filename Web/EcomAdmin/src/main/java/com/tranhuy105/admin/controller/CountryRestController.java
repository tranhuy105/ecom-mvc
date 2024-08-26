package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.service.CountryService;
import com.tranhuy105.common.entity.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CountryRestController {
    private final CountryService countryService;

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> findAllCountry() {
        return ResponseEntity.ok(countryService.findAll());
    }

    @PostMapping("/countries")
    public ResponseEntity<Country> saveNewCountry(@RequestBody Country country) {
        return ResponseEntity.ok(countryService.save(country));
    }
}
