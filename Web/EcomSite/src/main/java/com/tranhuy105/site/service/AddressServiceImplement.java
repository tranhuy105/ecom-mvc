package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Address;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressServiceImplement implements AddressService {
    private final AddressRepository addressRepository;

    @Override
    public List<Address> findByCustomerId(Integer id) {
        return addressRepository.findByCustomerId(id);
    }

    @Override
    public Address findById(Integer id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public Address findMainAddressForCustomer(Customer customer) {
        List<Address> addresses = findByCustomerId(customer.getId());

        if (addresses.isEmpty()) {
            return null;
        }

        for (Address address : addresses) {
            if (address.isMainAddress()) {
                return address;
            }
        }

        return addresses.get(0);
    }

    @Override
    @Transactional
    public void updateAddress(Address address, Customer customer) {
        List<Address> addressList = findByCustomerId(customer.getId());
        Address existingAddress = addressList
                .stream()
                .filter(a -> a.getId().equals(address.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Either the id is wrong or you dont have the permission to perform this action"));

        existingAddress.setAddressLine1(address.getAddressLine1());
        existingAddress.setAddressLine2(address.getAddressLine2());
        existingAddress.setCity(address.getCity());
        existingAddress.setState(address.getState());
        existingAddress.setPostalCode(address.getPostalCode());
        existingAddress.setCustomer(address.getCustomer());
        existingAddress.setCountry(address.getCountry());

        addressRepository.save(existingAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Integer id, Customer customer) {
        List<Address> addressList = findByCustomerId(customer.getId());
        Address deletedAddress = addressList
                .stream()
                .filter(a->a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Either the id is wrong or you dont have the permission to perform this action"));

        if (deletedAddress.isMainAddress()) {
            throw new IllegalArgumentException("Can not delete main address");
        }

        addressRepository.delete(deletedAddress);
    }

    @Override
    @Transactional
    public void createNewAddress(Address address, Customer customer) {
        address.setCustomer(customer);
        validateAddress(address);

        List<Address> customerAddresses = addressRepository.findByCustomerId(customer.getId());
        if (customerAddresses.isEmpty()) {
            address.setMainAddress(true);
        } else if (address.isMainAddress()) {
            for (Address addr : customerAddresses) {
                if (addr.isMainAddress()) {
                    addr.setMainAddress(false);
                    addressRepository.save(addr);
                }
            }
        }
        addressRepository.save(address);
    }

    @Transactional
    @Override
    public void updateDefaultAddress(Integer addressId, Customer customer) {
        List<Address> addressList = findByCustomerId(customer.getId());
        boolean found = false;
        for (Address address : addressList) {
            if (address.getId().equals(addressId)) {
                if (!found) {
                    address.setMainAddress(true);
                    found = true;
                }
            } else {
                address.setMainAddress(false);
            }
        }

        if (!found) throw new IllegalArgumentException("Either the id is wrong or you dont have the permission to perform this action");

        addressRepository.saveAll(addressList);
    }

    private void validateAddress(Address address) {
        if (address.getAddressLine1() == null || address.getAddressLine1().trim().isEmpty()) {
            throw new IllegalArgumentException("Address line 1 must not be empty.");
        }

        if (address.getAddressLine2() != null && address.getAddressLine2().trim().isEmpty()) {
            address.setAddressLine2("");
        }

        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City must not be empty.");
        }

        if (address.getState() == null || address.getState().trim().isEmpty()) {
            throw new IllegalArgumentException("State must not be empty.");
        }

        if (address.getPostalCode() == null || address.getPostalCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Postal code must not be empty.");
        } else if (address.getPostalCode().length() > 10) {
            throw new IllegalArgumentException("Postal code must not exceed 10 characters.");
        }

        if (address.getCountry() == null || address.getCountry().getId() == null) {
            throw new IllegalArgumentException("Country must be selected.");
        }

        if (address.getCustomer() == null || address.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer must be assigned to this address.");
        }
    }

}
