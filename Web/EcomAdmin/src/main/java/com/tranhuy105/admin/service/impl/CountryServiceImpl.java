package com.tranhuy105.admin.service.impl;

import com.tranhuy105.admin.repository.CountryRepository;
import com.tranhuy105.admin.service.CountryService;
import com.tranhuy105.common.entity.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public Country save(Country country) {
        return countryRepository.save(country);
    }
}
