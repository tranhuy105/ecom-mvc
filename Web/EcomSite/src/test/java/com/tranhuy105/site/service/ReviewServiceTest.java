package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.entity.OrderItem;
import com.tranhuy105.common.entity.Review;
import com.tranhuy105.common.entity.Sku;
import com.tranhuy105.site.dto.CreateReviewDTO;
import com.tranhuy105.site.dto.ReviewDTO;
import com.tranhuy105.site.dto.ReviewStatsDTO;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.OrderItemRepository;
import com.tranhuy105.site.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPageSize() {
        assertEquals(5, reviewService.getPageSize());
    }

    @Test
    void testFindByProduct() {
        Integer productId = 1;
        int page = 0;

        ReviewDTO review1 = mock(ReviewDTO.class);
        when(review1.getRating()).thenReturn(5);
        when(review1.getContent()).thenReturn("Good");
        when(review1.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(review1.getCustomerName()).thenReturn("John Doe");
        when(review1.getCustomerAvatar()).thenReturn("avatar.jpg");
        when(review1.getProductVariation()).thenReturn("Variation1");

        ReviewDTO review2 = mock(ReviewDTO.class);
        when(review2.getRating()).thenReturn(3);
        when(review2.getContent()).thenReturn("Average");
        when(review2.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(review2.getCustomerName()).thenReturn("Jane Doe");
        when(review2.getCustomerAvatar()).thenReturn("avatar2.jpg");
        when(review2.getProductVariation()).thenReturn("Variation2");

        List<ReviewDTO> expectedReviews = Arrays.asList(review1, review2);

        when(reviewRepository.findAllReviewByProductId(productId, PageRequest.of(page, reviewService.getPageSize())))
                .thenReturn(expectedReviews);

        List<ReviewDTO> reviews = reviewService.findByProduct(productId, page);

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals(5, reviews.get(0).getRating());
        assertEquals("Good", reviews.get(0).getContent());
        assertEquals("John Doe", reviews.get(0).getCustomerName());
        assertEquals("avatar.jpg", reviews.get(0).getCustomerAvatar());
    }

    @Test
    void testCreateReview() {
        CreateReviewDTO dto = new CreateReviewDTO();
        dto.setOrderItemId(1);
        dto.setContent("Great product!");
        dto.setRating(5);

        Customer customer = new Customer();
        OrderItem orderItem = new OrderItem();
        orderItem.setSku(new Sku());

        when(orderItemRepository.findByIdWithSku(dto.getOrderItemId()))
                .thenReturn(Optional.of(orderItem));

        reviewService.createReview(dto, customer);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());

        Review savedReview = reviewCaptor.getValue();
        assertEquals("Great product!", savedReview.getContent());
        assertEquals(5, savedReview.getRating());
        assertEquals(orderItem, savedReview.getOrderItem());
        assertEquals(customer, savedReview.getCustomer());
    }

    @Test
    void testCreateReview_OrderItemNotFound() {
        CreateReviewDTO dto = new CreateReviewDTO();
        dto.setOrderItemId(1);

        when(orderItemRepository.findByIdWithSku(dto.getOrderItemId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            reviewService.createReview(dto, new Customer());
        });

        assertEquals("Order Item Not Found", exception.getMessage());
    }

    @Test
    void testGetReviewStatsByProductId() {
        Integer productId = 1;
        List<Object[]> resultArray = Arrays.asList(new Object[][]{
                {10L, 4.5, 2L, 3L, 1L, 2L, 2L}
        });
        when(reviewRepository.getReviewStatsByProductId(productId))
                .thenReturn(resultArray);

        ReviewStatsDTO stats = reviewService.getReviewStatsByProductId(productId);

        assertNotNull(stats);
        assertEquals(4.5, stats.getAverageRating());
        assertEquals(10, stats.getTotalReviews());
        assertEquals(2, stats.getTotalPages());
        assertEquals(Arrays.asList(20, 20, 10, 30, 20), stats.getStarDistribution());
    }

    @Test
    void testGetReviewStatsByProductId_NoReviews() {
        Integer productId = 1;
        when(reviewRepository.getReviewStatsByProductId(productId))
                .thenReturn(Arrays.asList());

        ReviewStatsDTO stats = reviewService.getReviewStatsByProductId(productId);

        assertNotNull(stats);
        assertEquals(0.0, stats.getAverageRating());
        assertEquals(0, stats.getTotalReviews());
        assertEquals(0, stats.getTotalPages());
        assertEquals(Arrays.asList(0, 0, 0, 0, 0), stats.getStarDistribution());
    }
}
