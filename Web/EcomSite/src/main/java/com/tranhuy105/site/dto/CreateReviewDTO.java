package com.tranhuy105.site.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateReviewDTO {
    @NotNull
    Integer orderItemId;
    @Size(min = 1, max = 5)
    Integer rating;
    @Size(max = 5000, min = 20)
    String content;
}
