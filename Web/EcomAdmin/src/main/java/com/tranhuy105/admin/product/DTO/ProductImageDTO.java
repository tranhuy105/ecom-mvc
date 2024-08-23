package com.tranhuy105.admin.product.DTO;

import lombok.*;

@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductImageDTO {
    private Integer id;
    private String name;
    public String getPath() {
        return "/images/default_user.jpg";
    }
}
