package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Address;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.site.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImplement addressService;

    private Customer customer;
    private Country country;
    private Address address;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1);

        country = new Country();
        country.setId(1);

        address = new Address();
        address.setId(1);
        address.setAddressLine1("123 Main St");
        address.setAddressLine2("Apt 4B");
        address.setCity("Springfield");
        address.setState("IL");
        address.setPostalCode("62701");
        address.setCustomer(customer);
        address.setCountry(country);
        address.setMainAddress(false);
    }

    @Test
    void testFindByCustomerId() {
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);

        when(addressRepository.findByCustomerId(customer.getId())).thenReturn(addresses);

        List<Address> result = addressService.findByCustomerId(customer.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(address, result.get(0));
    }

    @Test
    void testFindById() {
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        Address result = addressService.findById(address.getId());

        assertNotNull(result);
        assertEquals(address, result);
    }

    @Test
    void testFindMainAddressForCustomer_NoMainAddress() {
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);

        when(addressRepository.findByCustomerId(customer.getId())).thenReturn(addresses);

        Address result = addressService.findMainAddressForCustomer(customer);

        assertNotNull(result);
        assertEquals(address, result);
    }

    @Test
    void testFindMainAddressForCustomer_WithMainAddress() {
        Address mainAddress = new Address();
        mainAddress.setId(2);
        mainAddress.setAddressLine1("456 Elm St");
        mainAddress.setCity("Springfield");
        mainAddress.setState("IL");
        mainAddress.setPostalCode("62702");
        mainAddress.setCustomer(customer);
        mainAddress.setCountry(country);
        mainAddress.setMainAddress(true);

        List<Address> addresses = new ArrayList<>();
        addresses.add(mainAddress);
        addresses.add(address);

        when(addressRepository.findByCustomerId(customer.getId())).thenReturn(addresses);

        Address result = addressService.findMainAddressForCustomer(customer);

        assertNotNull(result);
        assertTrue(result.isMainAddress());
        assertEquals(mainAddress, result);
    }

    @Test
    @Transactional
    void testUpdateAddress() {
        Address updatedAddress = new Address();
        updatedAddress.setId(1);
        updatedAddress.setAddressLine1("789 Oak St");
        updatedAddress.setAddressLine2("");
        updatedAddress.setCity("Springfield");
        updatedAddress.setState("IL");
        updatedAddress.setPostalCode("62703");
        updatedAddress.setCustomer(customer);
        updatedAddress.setCountry(country);

        when(addressRepository.findByCustomerId(customer.getId())).thenReturn(List.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);

        addressService.updateAddress(updatedAddress, customer);

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository, times(1)).save(addressCaptor.capture());

        Address capturedAddress = addressCaptor.getValue();
        assertEquals("789 Oak St", capturedAddress.getAddressLine1());
    }

    @Test
    @Transactional
    void testDeleteAddress() {
        when(addressRepository.findByCustomerId(customer.getId())).thenReturn(List.of(address));
        doNothing().when(addressRepository).delete(any(Address.class));

        addressService.deleteAddress(address.getId(), customer);

        verify(addressRepository, times(1)).delete(address);
    }

    @Test
    @Transactional
    void testCreateNewAddress() {
        Address newAddress = new Address();
        newAddress.setAddressLine1("123 Pine St");
        newAddress.setCity("Springfield");
        newAddress.setState("IL");
        newAddress.setPostalCode("62704");
        newAddress.setCustomer(customer);
        newAddress.setCountry(country);
        newAddress.setMainAddress(true);

        when(addressRepository.findByCustomerId(customer.getId())).thenReturn(new ArrayList<>());
        when(addressRepository.save(any(Address.class))).thenReturn(newAddress);

        addressService.createNewAddress(newAddress, customer);

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository, times(1)).save(addressCaptor.capture());

        Address capturedAddress = addressCaptor.getValue();
        assertTrue(capturedAddress.isMainAddress());
    }

    @Test
    @Transactional
    void testUpdateDefaultAddress() {
        Address address1 = new Address();
        address1.setId(1);
        address1.setMainAddress(true);

        Address address2 = new Address();
        address2.setId(2);
        address2.setMainAddress(false);

        List<Address> addresses = List.of(address1, address2);

        when(addressRepository.findByCustomerId(customer.getId())).thenReturn(addresses);
        when(addressRepository.saveAll(any(List.class))).thenReturn(addresses);

        addressService.updateDefaultAddress(2, customer);

        ArgumentCaptor<List<Address>> addressCaptor = ArgumentCaptor.forClass(List.class);
        verify(addressRepository, times(1)).saveAll(addressCaptor.capture());

        List<Address> capturedAddresses = addressCaptor.getValue();
        assertTrue(capturedAddresses.stream().anyMatch(Address::isMainAddress));
        assertEquals(2, capturedAddresses.size());
    }
}
