package com.tranhuy105.admin.service;

import com.tranhuy105.common.entity.Country;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CountryService {
    List<Country> findAll();

    @Transactional
    Country save(Country country);

    @Transactional
    void delete(Integer id);

    Country findById(Integer id);
}

