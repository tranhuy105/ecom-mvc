package com.tranhuy105.site.security;

import com.tranhuy105.common.entity.AuthenticationType;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.service.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class EmailLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        Customer customer = ((CustomerDetails) authentication.getPrincipal()).getCustomer();

        if (!AuthenticationType.EMAIL.equals(customer.getAuthenticationType())) {
            customerService.updateAuthenticationType(customer, AuthenticationType.EMAIL);
            customer.setAuthenticationType(AuthenticationType.EMAIL);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
