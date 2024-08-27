package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Address;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.dto.RegisterFormDTO;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.CountryRepository;
import com.tranhuy105.site.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<Country> findAvailableCountry() {
        return countryRepository.findAllOrderByName();
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
            throw new RuntimeException("Unexpected Error Occurred When Sending Verification Email");
        }
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
