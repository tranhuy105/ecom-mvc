package com.tranhuy105.site.repository;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.constant.ShippingStatus;
import com.tranhuy105.common.entity.*;
import com.tranhuy105.site.dto.ReviewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SkuRepository skuRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CountryRepository countryRepository;

    private final Integer productId = 1;

    @BeforeEach
    void setUp() {
        Sku sku = skuRepository.findById(1).orElseThrow();

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
        customer.setProfilePictureUrl("avatar");
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

        Order order = new Order();
        order.setOrderNumber("ORDER123");
        order.setCustomer(customer);
        order.setTotalAmount(BigDecimal.valueOf(200.00));
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setShippingAmount(BigDecimal.valueOf(20.00));
        order.setFinalAmount(BigDecimal.valueOf(220.00));
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingStatus(ShippingStatus.PENDING);
        order.setReservationExpiry(LocalDateTime.now().plusDays(1));
        order.setShippingAddress(customer.getAddresses().stream().findFirst().orElseThrow());
        order = orderRepository.save(order);

        for (int i = 1; i <= 5; i++) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setSku(sku);
            orderItem.setQuantity(1);
            orderItem.setTotalAmount(sku.getPrice());
            orderItem.setPrice(BigDecimal.valueOf(200.00));
            orderItem = orderItemRepository.save(orderItem);

            Review review = new Review();
            review.setCreatedAt(LocalDateTime.now());
            review.setContent("Review " + i);
            review.setRating(i);
            review.setSku(sku);
            review.setCustomer(customer);
            review.setOrderItem(orderItem);
            reviewRepository.save(review);
        }
    }

    @Test
    void testFindAllReviewByProductId() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ReviewDTO> reviews = reviewRepository.findAllReviewByProductId(productId, pageable);

        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
        assertEquals(5, reviews.size());

        ReviewDTO reviewDTO = reviews.get(0);
        assertEquals("Review 1", reviewDTO.getContent());
        assertEquals(1, reviewDTO.getRating());
        assertEquals("John Doe", reviewDTO.getCustomerName());
        assertNotNull(reviewDTO.getCustomerAvatar());
        assertEquals("DELL-XPS-15-1", reviewDTO.getProductVariation());
    }

    @Test
    void testGetReviewStatsByProductId() {
        List<Object[]> stats = reviewRepository.getReviewStatsByProductId(productId);

        assertNotNull(stats);
        assertFalse(stats.isEmpty());

        Object[] stat = stats.get(0);
        assertEquals(5L, stat[0]); // Total count
        assertEquals(3.0, stat[1]); // Average rating
        assertEquals(1L, stat[2]); // Count of 5-star reviews
        assertEquals(1L, stat[3]); // Count of 4-star reviews
        assertEquals(1L, stat[4]); // Count of 3-star reviews
        assertEquals(1L, stat[5]); // Count of 2-star reviews
        assertEquals(1L, stat[6]); // Count of 1-star reviews
    }
}
