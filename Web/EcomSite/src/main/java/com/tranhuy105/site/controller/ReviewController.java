package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.dto.CreateReviewDTO;
import com.tranhuy105.site.dto.ReviewDTO;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.service.CustomerService;
import com.tranhuy105.site.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final CustomerService customerService;
    private final ReviewService reviewService;

    @GetMapping("/products/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getProductReviews(@PathVariable Integer id,
                                                             @RequestParam(value = "page", defaultValue = "1") int page) {
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
//        for (int i = (page - 1) * 5 + 1; i <= (page - 1) * 5 + 5 && i <= 102; i++) {
//            int finalI = i;
//            ReviewDTO review = new ReviewDTO() {
//                @Override
//                public Integer getRating() {
//                    return (finalI % 5) + 1;
//                }
//
//                @Override
//                public String getContent() {
//                    return "This is a mock review number " + finalI + ". The product is excellent!";
//                }
//
//                @Override
//                public LocalDateTime getCreatedAt() {
//                    return LocalDateTime.now().minusDays(finalI);
//                }
//
//                @Override
//                public String getCustomerName() {
//                    return "Customer " + finalI;
//                }
//
//                @Override
//                public String getCustomerAvatar() {
//                    return "https://i.pravatar.cc/150?img=" + (finalI % 50);
//                }
//
//                @Override
//                public String getProductVariation() {
//                    if (finalI % 5 == 0) {
//                        return "BAN-DAC-BIET";
//                    } else {
//                        return "BAN-PHO-THONG";
//                    }
//                }
//
//                @Override
//                public String getFormatCreatedAt() {
//                    return getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
//                }
//            };
//            reviewDTOs.add(review);
//        }
//        return ResponseEntity.ok(reviewDTOs);

        return ResponseEntity.ok(reviewService.findByProduct(id, page - 1));
    }

    @PostMapping("/reviews")
    public ResponseEntity<Void> createReview(@RequestBody CreateReviewDTO dto,
                                             Authentication authentication) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            reviewService.createReview(dto, customer);
        } catch (NotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
