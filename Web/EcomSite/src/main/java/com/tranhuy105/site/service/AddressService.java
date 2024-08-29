package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Address;
import com.tranhuy105.common.entity.Customer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressService {
    List<Address> findByCustomerId(Integer customerId);
    Address findById(Integer id);
    @Transactional
    void deleteAddress(Integer id, Customer customer);
    @Transactional
    void updateAddress(Address address, Customer customer);
    @Transactional
    void createNewAddress(Address address, Customer customer);

    @Transactional
    void updateDefaultAddress(Integer addressId, Customer customer);
}
