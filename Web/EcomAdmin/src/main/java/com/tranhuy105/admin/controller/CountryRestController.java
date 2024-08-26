package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.service.CountryService;
import com.tranhuy105.common.entity.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CountryRestController {
    private final CountryService countryService;

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> findAllCountry() {
        return ResponseEntity.ok(countryService.findAll());
    }

    @PostMapping("/countries")
    public ResponseEntity<Country> saveCountry(@RequestBody Country country) {
        Country savedCountry;
        try {
            savedCountry = countryService.save(country);
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException exception) {
            log.error("UNEXPECTED EXCEPTION OCCURRED WHILE SAVING COUNTRY", exception);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(savedCountry);
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Integer id) {
        countryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
