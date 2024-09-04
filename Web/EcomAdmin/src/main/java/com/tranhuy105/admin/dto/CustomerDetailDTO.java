package com.tranhuy105.admin.dto;

import java.time.LocalDateTime;

public interface CustomerDetailDTO {
    Integer getId();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getPhoneNumber();
    boolean isEnabled();
    LocalDateTime getLastLoginAt();
    String getProfilePictureUrl();
    LocalDateTime getDateOfBirth();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getAuthenticationType();
    Long getAddressCount();
    Long getOrderCount();
    Double getTotalSpent();
}
