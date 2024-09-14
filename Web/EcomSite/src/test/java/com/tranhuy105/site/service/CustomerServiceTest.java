package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.AuthenticationType;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.dto.AccountDTO;
import com.tranhuy105.site.dto.RegisterFormDTO;
import com.tranhuy105.site.security.CustomerDetails;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.CountryRepository;
import com.tranhuy105.site.repository.CustomerRepository;
import com.tranhuy105.site.security.CustomerOAuth2User;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private MailService mailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAvailableCountry() {
        Country country = new Country();
        List<Country> countries = Arrays.asList(country);

        when(countryRepository.findAllOrderByName()).thenReturn(countries);

        List<Country> result = customerService.findAvailableCountry();

        assertNotNull(result);
        assertEquals(countries, result);
    }

    @Test
    void testFindByEmail() {
        Customer customer = new Customer();
        String email = "test@example.com";

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        Customer result = customerService.findByEmail(email);

        assertNotNull(result);
        assertEquals(customer, result);
    }

    @Test
    void testRegisterCustomer() {
        RegisterFormDTO registerFormDTO = new RegisterFormDTO();
        registerFormDTO.setEmail("test@example.com");
        registerFormDTO.setPassword("password");
        registerFormDTO.setFirstName("John");
        registerFormDTO.setLastName("Doe");

        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPassword("encoded_password");

        when(customerRepository.findByEmail(registerFormDTO.getEmail())).thenReturn(Optional.empty());
        when(mailService.generateVerificationCode()).thenReturn("verification_code");
        when(passwordEncoder.encode(registerFormDTO.getPassword())).thenReturn("encoded_password");
        when(countryRepository.findById(registerFormDTO.getCountry())).thenReturn(Optional.of(new Country()));

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerService.registerCustomer(registerFormDTO);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();

        assertNotNull(savedCustomer);
        assertEquals("test@example.com", savedCustomer.getEmail());
        assertEquals("encoded_password", savedCustomer.getPassword());
    }

    @Test
    void testRegisterCustomer_EmailAlreadyExists() {
        RegisterFormDTO registerFormDTO = new RegisterFormDTO();
        registerFormDTO.setEmail("test@example.com");

        when(customerRepository.findByEmail(registerFormDTO.getEmail())).thenReturn(Optional.of(new Customer()));

        assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(registerFormDTO);
        });
    }

    @Test
    void testVerifyAccount() {
        Customer customer = new Customer();
        customer.setEnabled(false);
        String validCode = "1234567890abcdef1234567890abcdef";

        when(customerRepository.findByVerificationCode(validCode)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).enable(customer.getId());

        customerService.verifyAccount(validCode);

        verify(customerRepository).enable(customer.getId());
    }

    @Test
    void testVerifyAccount_InvalidCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.verifyAccount("short_code");
        });
    }

    @Test
    void testUpdate() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1);
        accountDTO.setFirstName("Jane");
        accountDTO.setLastName("Doe");
        accountDTO.setPassword("new_password");

        Customer customer = new Customer();
        customer.setId(1);
        customer.setPassword("old_password");
        customer.setAuthenticationType(AuthenticationType.EMAIL);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_password");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer updatedCustomer = customerService.update(accountDTO);

        assertNotNull(updatedCustomer);
        assertEquals("Jane", updatedCustomer.getFirstName());
        assertEquals("encoded_password", updatedCustomer.getPassword());
    }

    @Test
    void testUpdate_CustomerNotFound() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1);

        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            customerService.update(accountDTO);
        });
    }

    @Test
    void testUpdateAuthenticationType() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setAuthenticationType(AuthenticationType.EMAIL);

        doNothing().when(customerRepository).updateAuthenticationType(customer.getId(), AuthenticationType.FACEBOOK);

        customerService.updateAuthenticationType(customer, AuthenticationType.FACEBOOK);

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<AuthenticationType> authTypeCaptor = ArgumentCaptor.forClass(AuthenticationType.class);

        verify(customerRepository).updateAuthenticationType(idCaptor.capture(), authTypeCaptor.capture());

        assertEquals(1, idCaptor.getValue());
        assertEquals(AuthenticationType.FACEBOOK, authTypeCaptor.getValue());
    }

    @Test
    void testUpdateAuthenticationType_NoOp() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setAuthenticationType(AuthenticationType.EMAIL);

        doNothing().when(customerRepository).updateAuthenticationType(customer.getId(), AuthenticationType.FACEBOOK);

        customerService.updateAuthenticationType(customer, AuthenticationType.FACEBOOK);

        verify(customerRepository).updateAuthenticationType(customer.getId(), AuthenticationType.FACEBOOK);
    }

    @Test
    void testRequestToResetPassword() throws MessagingException, UnsupportedEncodingException {
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setAuthenticationType(AuthenticationType.EMAIL);
        customer.setEnabled(true);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(mailService.generateVerificationCode()).thenReturn("reset_token");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerService.requestToResetPassword(email);

        verify(mailService).sendResetPasswordMail(customer);
    }

    @Test
    void testRequestToResetPassword_CustomerNotFound() {
        String email = "test@example.com";

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            customerService.requestToResetPassword(email);
        });
    }

    @Test
    void testResetPassword() {
        String resetPasswordToken = "valid_token";
        String newPassword = "new_password";
        Customer customer = new Customer();
        customer.setForgotPasswordCode(resetPasswordToken);

        when(customerRepository.findByForgotPasswordCode(resetPasswordToken)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded_password");

        customerService.resetPassword(resetPasswordToken, newPassword);

        assertNull(customer.getForgotPasswordCode());
        assertEquals("encoded_password", customer.getPassword());
    }

    @Test
    void testResetPassword_CustomerNotFound() {
        String resetPasswordToken = "invalid_token";

        when(customerRepository.findByForgotPasswordCode(resetPasswordToken)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            customerService.resetPassword(resetPasswordToken, "new_password");
        });
    }

    @Test
    void testGetCustomerFromAuthentication() {
        Customer customer = new Customer();
        customer.setEmail("test@example.com");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomerDetails(customer), null, null
        );

        Customer result = customerService.getCustomerFromAuthentication(authentication);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetCustomerFromAuthentication_OAuth2() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("email")).thenReturn("oauth2@example.com");
        CustomerOAuth2User oauth2User = new CustomerOAuth2User(oAuth2User);
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                oauth2User, null, "client"
        );
        Customer customer = new Customer();
        customer.setEmail("oauth2@example.com");

        when(customerRepository.findByEmail("oauth2@example.com")).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerFromAuthentication(authentication);

        assertNotNull(result);
        assertEquals("oauth2@example.com", result.getEmail());
    }


    @Test
    void testGetCustomerFromAuthentication_InvalidType() {
        Authentication authentication = mock(Authentication.class);

        Customer result = customerService.getCustomerFromAuthentication(authentication);

        assertNull(result);
    }
}
