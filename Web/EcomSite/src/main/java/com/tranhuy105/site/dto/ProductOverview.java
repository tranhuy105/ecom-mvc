package com.tranhuy105.site.dto;

import java.math.BigDecimal;

public interface ProductOverview {
    Integer getId();
    String getName();
    String getAlias();
    String getShortDescription();
    BigDecimal getPrice();
    BigDecimal getDiscountPercent();
    BigDecimal getRating();
    Integer getReviewsCount();
    String getImagePath();
}
