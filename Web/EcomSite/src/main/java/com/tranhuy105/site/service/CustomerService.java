package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.AuthenticationType;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.dto.AccountDTO;
import com.tranhuy105.site.security.CustomerOAuth2User;
import com.tranhuy105.site.dto.RegisterFormDTO;
import org.springframework.security.core.Authentication;
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

    @Transactional
    Customer update(AccountDTO accountDTO);

    void updateAuthenticationType(Customer customer, AuthenticationType newAuthenticateType);
    @Transactional
    void requestToResetPassword(String email);
    @Transactional
    void resetPassword(String resetPasswordToken, String newPassword);

    Customer findByResetPasswordCode(String code);

    Customer getCustomerFromAuthentication(Authentication authentication);
}
