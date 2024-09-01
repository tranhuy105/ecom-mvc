package com.tranhuy105.admin.dto;

import lombok.*;

@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductImageDTO {
    private Integer id;
    private String name;
    public String getPath() {
        return this.name;
    }
}
