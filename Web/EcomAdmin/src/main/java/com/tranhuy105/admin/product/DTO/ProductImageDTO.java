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
        if (id != null && id > 10 && name != null) {
            return "/product-images/"+this.name;
        }
        return "/images/default_user.jpg";
    }
}
