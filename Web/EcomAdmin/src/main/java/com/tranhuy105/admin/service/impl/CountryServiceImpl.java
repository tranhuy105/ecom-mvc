package com.tranhuy105.admin.service.impl;

import com.tranhuy105.admin.repository.CountryRepository;
import com.tranhuy105.admin.service.CountryService;
import com.tranhuy105.common.entity.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public List<Country> findAll() {
        return countryRepository.findAllOrderByName();
    }

    @Override
    @Transactional
    public Country save(Country country) {
        if (country.getName() == null || country.getName().isBlank() || country.getName().length() > 45) {
            throw new IllegalArgumentException();
        }

        if (country.getCode() == null || country.getCode().isBlank() || country.getCode().length() > 5) {
            throw new IllegalArgumentException();
        }

        return countryRepository.save(country);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        countryRepository.deleteById(id);
    }

    @Override
    public Country findById(Integer id) {
        return countryRepository.findById(id).orElse(null);
    }
}
