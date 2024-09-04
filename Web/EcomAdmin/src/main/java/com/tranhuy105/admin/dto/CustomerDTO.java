package com.tranhuy105.admin.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface CustomerDTO {

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

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    default String getPfp() {
        if (getProfilePictureUrl() == null) {
            return "/images/default_user.jpg";
        }

        return getProfilePictureUrl();
    }

    default String formatLocalDate(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }
}