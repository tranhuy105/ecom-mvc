package com.tranhuy105.admin.product.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private Integer id;
    private String name;
    private Integer productId;
}
