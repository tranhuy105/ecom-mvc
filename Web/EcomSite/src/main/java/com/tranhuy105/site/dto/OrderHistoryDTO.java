package com.tranhuy105.site.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderHistoryDTO {
    private Integer id;
    private String status;
    private LocalDateTime changedAt;

    public String getFormattedChangedAt() {
        return changedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }
}
