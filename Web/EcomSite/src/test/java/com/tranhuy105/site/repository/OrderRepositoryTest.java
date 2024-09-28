package com.tranhuy105.site.repository;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.constant.ShippingStatus;
import com.tranhuy105.common.entity.Address;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.dto.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CountryRepository countryRepository;

    private Order testOrder;
    private Integer thisCustomerId;

    @BeforeEach
    void setUp() {
        Country country = new Country();
        country.setCode("VN");
        country.setName("VIET NAM");
        country = countryRepository.save(country);

        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPassword("password");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEnabled(false);
        customer.setVerificationCode("1234567890abcdef");
        customer.setForgotPasswordCode("forgot123");

        Address address = new Address();
        address.setAddressLine1("123 Test St");
        address.setCity("Test City");
        address.setState("TS");
        address.setPostalCode("12345");
        address.setCustomer(customer);
        address.setCountry(country);
        customer.setAddresses(Set.of(address));

        customer = customerRepository.save(customer);
        thisCustomerId = customer.getId();


        testOrder = new Order();
        testOrder.setOrderNumber("ORDER123");
        testOrder.setCustomer(customer);
        testOrder.setShippingAddress(customer.getAddresses().stream().findFirst().orElseThrow());
        testOrder.setTotalAmount(BigDecimal.valueOf(200.00));
        testOrder.setDiscountAmount(BigDecimal.ZERO);
        testOrder.setShippingAmount(BigDecimal.valueOf(20.00));
        testOrder.setFinalAmount(BigDecimal.valueOf(220.00));
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setReservationExpiry(LocalDateTime.now().plusDays(1));

        orderRepository.save(testOrder);
    }

    @Test
    void testFindOrderDTOsByCustomerId() {
        List<OrderDTO> orders = orderRepository.findOrderDTOsByCustomerId(thisCustomerId);
        assertFalse(orders.isEmpty());
        assertEquals("ORDER123", orders.get(0).getOrderNumber());
    }

    @Test
    void testFindByOrderNumberWithPayment() {
        Optional<Order> orderOptional = orderRepository.findByOrderNumberWithPayment("ORDER123");
        assertTrue(orderOptional.isPresent());
        assertEquals("ORDER123", orderOptional.get().getOrderNumber());
    }

    @Test
    void testFindExpiredOrders() {
        List<Order> expiredOrders = orderRepository.findExpiredOrders(LocalDateTime.now().minusDays(2));
        assertTrue(expiredOrders.isEmpty());

        testOrder.setReservationExpiry(LocalDateTime.now().minusDays(1));
        orderRepository.save(testOrder);

        expiredOrders = orderRepository.findExpiredOrders(LocalDateTime.now());
        assertFalse(expiredOrders.isEmpty());
    }

    @Test
    void testFindOrderDetail() {
        Optional<Order> orderOptional = orderRepository.findOrderDetail("ORDER123");
        assertTrue(orderOptional.isPresent());
        assertEquals("ORDER123", orderOptional.get().getOrderNumber());
    }

    @Test
    void testFindByOrderNumber() {
        Optional<Order> orderOptional = orderRepository.findByOrderNumber("ORDER123");
        assertTrue(orderOptional.isPresent());
        assertEquals("ORDER123", orderOptional.get().getOrderNumber());
    }

    @Test
    void testLazyFetchItem() {
        Order order = orderRepository.lazyFetchItem(testOrder);
        assertNotNull(order.getOrderItems());
    }
}