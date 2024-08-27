package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.AuthenticationType;
import com.tranhuy105.common.entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager entityManager;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPassword("password");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEnabled(false);
        customer.setVerificationCode("1234567890abcdef");

        customer = customerRepository.save(customer);
    }

    @Test
    void testFindByEmail() {
        Optional<Customer> foundCustomer = customerRepository.findByEmail("test@example.com");
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getEmail(), foundCustomer.get().getEmail());
    }

    @Test
    void testCreateCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setEmail("newcustomer@example.com");
        newCustomer.setPassword("newpassword");
        newCustomer.setFirstName("Alice");
        newCustomer.setLastName("Wonderland");
        newCustomer.setEnabled(false);
        newCustomer.setVerificationCode("verify123");

        Customer savedCustomer = customerRepository.save(newCustomer);

        assertNotNull(savedCustomer.getId());
        Optional<Customer> fetchedCustomer = customerRepository.findById(savedCustomer.getId());

        assertTrue(fetchedCustomer.isPresent());
        assertEquals("Alice", fetchedCustomer.get().getFirstName());
        assertEquals("Wonderland", fetchedCustomer.get().getLastName());
        assertEquals("newcustomer@example.com", fetchedCustomer.get().getEmail());
        assertFalse(fetchedCustomer.get().isEnabled());
        assertEquals("verify123", fetchedCustomer.get().getVerificationCode());
    }

    @Test
    void testDeleteCustomer() {
        Customer customerToDelete = new Customer();
        customerToDelete.setEmail("delete@example.com");
        customerToDelete.setPassword("deletepassword");
        customerToDelete.setFirstName("Delete");
        customerToDelete.setLastName("Me");
        customerToDelete.setEnabled(false);

        customerToDelete = customerRepository.save(customerToDelete);

        Integer id = customerToDelete.getId();
        assertNotNull(id);

        customerRepository.deleteById(id);

        Optional<Customer> deletedCustomer = customerRepository.findById(id);
        assertFalse(deletedCustomer.isPresent());
    }


    @Test
    void testFindByVerificationCode() {
        Optional<Customer> foundCustomer = customerRepository.findByVerificationCode("1234567890abcdef");
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getVerificationCode(), foundCustomer.get().getVerificationCode());
    }

    @Test
    void testEnable() {
        customerRepository.enable(customer.getId());
        entityManager.clear();

        Optional<Customer> enabledCustomer = customerRepository.findById(customer.getId());

        assertTrue(enabledCustomer.isPresent());
        assertTrue(enabledCustomer.get().isEnabled());
        assertNull(enabledCustomer.get().getVerificationCode());
    }

    @Test
    void testUpdateAuthenticationType() {
        AuthenticationType newAuthType = AuthenticationType.GOOGLE;

        customerRepository.updateAuthenticationType(customer.getId(), newAuthType);
        entityManager.clear();

        Optional<Customer> updatedCustomer = customerRepository.findById(customer.getId());
        assertTrue(updatedCustomer.isPresent());
        assertEquals(newAuthType, updatedCustomer.get().getAuthenticationType());
    }

}
