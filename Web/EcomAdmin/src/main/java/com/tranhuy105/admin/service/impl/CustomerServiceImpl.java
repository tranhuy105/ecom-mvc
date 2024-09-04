package com.tranhuy105.admin.service.impl;

import com.tranhuy105.admin.dto.*;
import com.tranhuy105.admin.repository.CustomerRepository;
import com.tranhuy105.admin.service.CustomerService;
import com.tranhuy105.common.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void disableCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        customer.setEnabled(false);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void enableCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        customer.setEnabled(true);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found");
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public Customer findById(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(null);
    }

    @Override
    public CustomerDetailDTO getCustomerDetails(Integer customerId) {
        return customerRepository.findCustomerDetailById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    @Override
    public Page<CustomerDTO> findAll(String search, Boolean enabled, String sortBy, String sortDirection, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, getSort(sortBy, sortDirection));
        return customerRepository.findAllCustomer(pageable, search, enabled);
    }

    @Override
    @Transactional
    public void createCustomer(Customer customer) {
        // Implement customer creation logic
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (customer.getId()!= null) {
            customer.setId(null);
        }
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setEnabled(true);
        customerRepository.save(customer);
    }
//
//    @Override
//    @Transactional
//    public void updateCustomer(Integer customerId, UpdateCustomerDTO updateCustomerDTO) {
//        Customer customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
//
//        if (updateCustomerDTO.getFirstName() != null) {
//            customer.setFirstName(updateCustomerDTO.getFirstName());
//        }
//        if (updateCustomerDTO.getLastName() != null) {
//            customer.setLastName(updateCustomerDTO.getLastName());
//        }
//        if (updateCustomerDTO.getPhoneNumber() != null) {
//            customer.setPhoneNumber(updateCustomerDTO.getPhoneNumber());
//        }
//        if (updateCustomerDTO.getDateOfBirth() != null) {
//            customer.setDateOfBirth(updateCustomerDTO.getDateOfBirth());
//        }
//        if (updateCustomerDTO.getEnabled() != null) {
//            customer.setEnabled(updateCustomerDTO.getEnabled());
//        }
//
//        customerRepository.save(customer);
//    }

    private Sort getSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Sort.Direction direction = ("asc".equalsIgnoreCase(sortDirection)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
