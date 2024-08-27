package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Address;
import com.tranhuy105.common.entity.AuthenticationType;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.dto.AccountDTO;
import com.tranhuy105.site.security.CustomerOAuth2User;
import com.tranhuy105.site.dto.RegisterFormDTO;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.CountryRepository;
import com.tranhuy105.site.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<Country> findAvailableCountry() {
        return countryRepository.findAllOrderByName();
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    @Override
    public void registerCustomer(RegisterFormDTO registerFormDTO) {
        Customer newCustomer = extractCustomerFromRegisterDTO(registerFormDTO);
        encodePassword(newCustomer);

        customerRepository.save(newCustomer);
        try {
            mailService.sendVerificationEmail(newCustomer);
        } catch (Exception exception) {
            log.error("Something went wrong when sending email: ", exception);
            throw new RuntimeException("Unexpected Error Occurred When Sending Verification Email");
        }
    }

    @Override
    public void registerOauth2User(CustomerOAuth2User oAuth2User) {
        Customer customer = new Customer();
        customer.setEmail(oAuth2User.getEmail());
        customer.setFirstName(oAuth2User.getFirstName());
        customer.setLastName(oAuth2User.getLastName());
        customer.setEnabled(true);
        customer.setPassword("");
        customer.setAuthenticationType(AuthenticationType.GOOGLE);
        customer.setProfilePictureUrl(oAuth2User.getProfilePicture());

        customerRepository.save(customer);
    }

    @Override
    public void verifyAccount(@NonNull String code) {
        if (code.length() != 32) {
            throw new IllegalArgumentException("Invalid Code");
        }

        Customer customerToVerify = customerRepository.findByVerificationCode(code).orElseThrow(
                () -> new NotFoundException("")
        );

        if (customerToVerify.isEnabled()) {
          throw new IllegalArgumentException("This account has already been verify");
        }

        customerRepository.enable(customerToVerify.getId());
    }

    @Transactional
    @Override
    public Customer update(AccountDTO accountDTO) {
        Customer customerInDB = customerRepository.findById(accountDTO.getId())
                .orElseThrow(() -> new NotFoundException(""));

        customerInDB.setFirstName(accountDTO.getFirstName());
        customerInDB.setLastName(accountDTO.getLastName());
        customerInDB.setPhoneNumber(accountDTO.getPhoneNumber());

        if (customerInDB.getAuthenticationType().equals(AuthenticationType.EMAIL)) {
            if (!customerInDB.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(accountDTO.getPassword());
                customerInDB.setPassword(encodedPassword);
            }
        }

        return customerRepository.save(customerInDB);
    }

    @Override
    public void updateAuthenticationType(Customer customer, AuthenticationType newAuthenticateType) {
        if (customer.getId() == null || newAuthenticateType.equals(customer.getAuthenticationType())) {
            return;
        }

        customerRepository.updateAuthenticationType(customer.getId(), newAuthenticateType);
    }

    @Transactional
    @Override
    public void requestToResetPassword(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(""));

        if (!customer.getAuthenticationType().equals(AuthenticationType.EMAIL) || !customer.isEnabled()) {
            throw new NotFoundException("");
        }

        String token = mailService.generateVerificationCode();
        customer.setForgotPasswordCode(token);
        Customer forgotCustomer = customerRepository.save(customer);

        try {
            mailService.sendResetPasswordMail(forgotCustomer);
        } catch (Exception exception) {
            log.error("Something went wrong when sending email: ", exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    @Transactional
    public void resetPassword(String resetPasswordToken, String newPassword) {
        Customer customer = customerRepository.findByForgotPasswordCode(resetPasswordToken)
                .orElseThrow(() -> new NotFoundException(""));

        customer.setPassword(passwordEncoder.encode(newPassword));
        customer.setForgotPasswordCode(null);
    }

    @Override
    public Customer findByResetPasswordCode(String code) {
        return customerRepository.findByForgotPasswordCode(code).orElse(null);
    }

    private Customer extractCustomerFromRegisterDTO(RegisterFormDTO registerFormDTO) {
        if (customerRepository.findByEmail(registerFormDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This Email Has Already Associated With Another Customer");
        }

        Customer customer = new Customer();
        customer.setVerificationCode(mailService.generateVerificationCode());
        customer.setEmail(registerFormDTO.getEmail());
        customer.setEnabled(false);
        customer.setPassword(registerFormDTO.getPassword());
        customer.setFirstName(registerFormDTO.getFirstName());
        customer.setLastName(registerFormDTO.getLastName());
        customer.setPhoneNumber(registerFormDTO.getPhoneNumber());

        Address mainAddress = new Address();
        mainAddress.setMainAddress(true);
        mainAddress.setAddressLine1(registerFormDTO.getAddressLine1());
        mainAddress.setAddressLine2(registerFormDTO.getAddressLine2());
        mainAddress.setCity(registerFormDTO.getCity());
        mainAddress.setCountry(countryRepository.findById(registerFormDTO.getCountry())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Country"))
        );
        mainAddress.setState(registerFormDTO.getState());
        mainAddress.setPostalCode(registerFormDTO.getPostalCode());
        mainAddress.setCustomer(customer);
        customer.getAddresses().add(mainAddress);
        return customer;
    }

    private void encodePassword(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
    }
}
