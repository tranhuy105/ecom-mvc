package com.tranhuy105.admin.service;

import com.tranhuy105.admin.dto.CustomerDTO;
import com.tranhuy105.admin.dto.CustomerDetailDTO;
import com.tranhuy105.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerService {
    void disableCustomer(Integer customerId);

    @Transactional
    void enableCustomer(Integer customerId);

    @Transactional
    void deleteCustomer(Integer customerId);

    Customer findById(Integer customerId);

    Page<CustomerDTO> findAll(String search, Boolean enabled, String sortBy, String sortDirection, int page, int size);

    CustomerDetailDTO getCustomerDetails(Integer customerId);

    @Transactional
    void createCustomer(Customer customer);

//    @Transactional
//    void createCustomer(CreateCustomerDTO createCustomerDTO);
//
//    @Transactional
//    void updateCustomer(Integer customerId, UpdateCustomerDTO updateCustomerDTO);
}
