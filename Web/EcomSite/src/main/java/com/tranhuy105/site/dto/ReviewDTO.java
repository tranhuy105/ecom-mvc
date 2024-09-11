package com.tranhuy105.site.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface ReviewDTO {
    Integer getRating();
    String getContent();
    LocalDateTime getCreatedAt();
    String getCustomerName();
    String getCustomerAvatar();
    String getProductVariation();

    default String getFormatCreatedAt(){
        return "";
    }
}
