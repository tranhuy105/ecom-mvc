package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.dto.CreateReviewDTO;
import com.tranhuy105.site.dto.ReviewDTO;
import com.tranhuy105.site.dto.ReviewStatsDTO;

import java.util.List;

public interface ReviewService {
    Integer getPageSize();

    List<ReviewDTO> findByProduct(Integer productId, int page);

    void createReview(CreateReviewDTO dto, Customer customer);

    ReviewStatsDTO getReviewStatsByProductId(Integer productId);
}
