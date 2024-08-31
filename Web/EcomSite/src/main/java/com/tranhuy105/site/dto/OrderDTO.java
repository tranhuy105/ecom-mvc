package com.tranhuy105.site.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderDTO {
    String getOrderNumber();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getAddressLine1();
    String getAddressLine2();
    String getCity();
    String getState();
    String getPostalCode();
    BigDecimal getAmount();
    String getOrderStatus();
    default String getAddressLine() {
        StringBuilder addressString = new StringBuilder();
        addressString.append(getAddressLine1());
        if (getAddressLine2() != null && !getAddressLine2().isEmpty()) {
            addressString.append(", ").append(getAddressLine2());
        }
        addressString.append(", ").append(getCity());
        addressString.append(", ").append(getState());
        addressString.append(" ").append(getPostalCode());
        return addressString.toString();
    }
}
