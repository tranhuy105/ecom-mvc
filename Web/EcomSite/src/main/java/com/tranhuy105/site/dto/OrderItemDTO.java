package com.tranhuy105.site.dto;

import java.math.BigDecimal;

public interface OrderItemDTO {
    Integer getId();
    String getSkuCode();
    String getProductName();
    String getProductAlias();
    String getProductImage();
    Integer getQuantity();
    BigDecimal getOriginalPrice();
    BigDecimal getFinalAmount();
    BigDecimal getDiscountedPercent();
}
