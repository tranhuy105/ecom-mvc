package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.AuthenticationType;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.security.CustomerOAuth2User;
import com.tranhuy105.site.dto.RegisterFormDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerService {
    List<Country> findAvailableCountry();

    Customer findByEmail(String email);

    @Transactional
    void registerCustomer(RegisterFormDTO registerFormDTO);

    @Transactional
    void registerOauth2User(CustomerOAuth2User oAuth2User);

    void verifyAccount(String code);

    void updateAuthenticationType(Customer customer, AuthenticationType newAuthenticateType);

}
