package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.entity.OrderItem;
import com.tranhuy105.common.entity.Review;
import com.tranhuy105.site.dto.CreateReviewDTO;
import com.tranhuy105.site.dto.ReviewDTO;
import com.tranhuy105.site.dto.ReviewStatsDTO;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.OrderItemRepository;
import com.tranhuy105.site.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Integer getPageSize() {
        return 5;
    }

    @Override
    public List<ReviewDTO> findByProduct(Integer productId, int page) {
        return reviewRepository.findAllReviewByProductId(productId, PageRequest.of(page, getPageSize()));
    }

    @Override
    public void createReview(CreateReviewDTO dto, Customer customer) {
        OrderItem orderItem = orderItemRepository.findByIdWithSku(dto.getOrderItemId())
                .orElseThrow(() -> new NotFoundException("Order Item Not Found"));

        Review review = new Review();
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        review.setOrderItem(orderItem);
        review.setSku(orderItem.getSku());
        review.setCustomer(customer);

        reviewRepository.save(review);
    }

    @Override
    public ReviewStatsDTO getReviewStatsByProductId(Integer productId) {
       List<Object[]> resultArray = reviewRepository.getReviewStatsByProductId(productId);

        if (!resultArray.isEmpty()) {
            Object[] result = resultArray.get(0);
            Long totalReviewsLong = (Long) result[0];
            int totalReviews = totalReviewsLong != null ? totalReviewsLong.intValue() : 0;
            double averageRating = (result[1] != null) ? ((Double) result[1]) : 0.0;

            List<Integer> starDistribution = new ArrayList<>(5);
            starDistribution.add(((Long) result[2]).intValue());
            starDistribution.add(((Long) result[3]).intValue());
            starDistribution.add(((Long) result[4]).intValue());
            starDistribution.add(((Long) result[5]).intValue());
            starDistribution.add(((Long) result[6]).intValue());

            int totalPages = (int) Math.ceil((double) totalReviews / getPageSize());

            return new ReviewStatsDTO(averageRating, totalReviews, totalPages, starDistribution);
        }

        return new ReviewStatsDTO(0.0, 0, 0, Arrays.asList(0, 0, 0, 0, 0));
    }

}
