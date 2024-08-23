package com.tranhuy105.admin.product.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SkuDTO {
    private Integer id;
    private String skuCode;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private Integer stockQuantity;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal weight;
    private Integer productId;
}
