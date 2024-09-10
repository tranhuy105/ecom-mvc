package com.tranhuy105.admin.dto;

import java.math.BigDecimal;

public interface ProductOverviewDTO {
    Integer getId();
    String getImagePath();
    String getName();
    String getCategory();
    Integer getCategoryId();
    String getBrand();
    Integer getBrandId();
    BigDecimal getPrice();
    BigDecimal getDiscountPercent();
    boolean isEnabled();
}
