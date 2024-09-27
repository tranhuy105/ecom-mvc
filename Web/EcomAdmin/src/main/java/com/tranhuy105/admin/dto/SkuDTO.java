package com.tranhuy105.admin.dto;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private BigDecimal cost;
    private BigDecimal discountPercent;
    private LocalDateTime saleStart;
    private LocalDateTime saleEnd;
    private Integer stockQuantity;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal weight;
    private Integer productId;
}
