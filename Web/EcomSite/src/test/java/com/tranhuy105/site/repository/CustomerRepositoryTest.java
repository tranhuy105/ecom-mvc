package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.*;
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
        customer.setForgotPasswordCode("forgot123");

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
    void testFindByForgotCode() {
        Optional<Customer> foundCustomer = customerRepository.findByForgotPasswordCode("forgot123");
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getForgotPasswordCode(), foundCustomer.get().getForgotPasswordCode());
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

    @Test
    void testFindByIdWithShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCustomer(customer);
        customer.setShoppingCart(shoppingCart);

        Sku sku1 = new Sku();
        sku1.setId(1);

        Sku sku2 = new Sku();
        sku2.setId(2);

        CartItem cartItem1 = new CartItem();
        cartItem1.setSku(sku1);
        cartItem1.setShoppingCart(shoppingCart);
        cartItem1.setQuantity(5);

        CartItem cartItem2 = new CartItem();
        cartItem2.setSku(sku2);
        cartItem2.setShoppingCart(shoppingCart);
        cartItem2.setQuantity(3);

        shoppingCart.getCartItems().add(cartItem1);
        shoppingCart.getCartItems().add(cartItem2);

        customerRepository.save(customer);
        entityManager.flush();
        entityManager.clear();

        Optional<Customer> foundCustomer = customerRepository.findByIdWithShoppingCart(customer.getId());

        // Assertions to verify the result
        assertTrue(foundCustomer.isPresent());
        Customer fetchedCustomer = foundCustomer.get();
        assertNotNull(fetchedCustomer.getShoppingCart());
        assertEquals(2, fetchedCustomer.getShoppingCart().getCartItems().size());

        CartItem fetchedCartItem1 = fetchedCustomer.getShoppingCart().getCartItems().stream()
                .filter(item -> item.getSku().getId().equals(cartItem1.getSku().getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(fetchedCartItem1);
        assertEquals(5, fetchedCartItem1.getQuantity());

        CartItem fetchedCartItem2 = fetchedCustomer.getShoppingCart().getCartItems().stream()
                .filter(item -> item.getSku().getId().equals(cartItem2.getSku().getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(fetchedCartItem2);
        assertEquals(3, fetchedCartItem2.getQuantity());
    }
}
