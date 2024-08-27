package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Country;
import com.tranhuy105.site.dto.RegisterFormDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerService {
    List<Country> findAvailableCountry();

    @Transactional
    void registerCustomer(RegisterFormDTO registerFormDTO);

    void verifyAccount(String code);
}
