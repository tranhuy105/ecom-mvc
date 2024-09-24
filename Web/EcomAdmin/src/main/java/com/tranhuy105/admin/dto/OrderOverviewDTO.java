package com.tranhuy105.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderOverviewDTO {
    String getOrderNumber();
    Integer getCustomerId();
    BigDecimal getTotalAmount();
    BigDecimal getFinalAmount();
    String getStatus();
    String getShippingStatus();
    String getPaymentStatus();
    LocalDateTime getCreatedAt();
}
