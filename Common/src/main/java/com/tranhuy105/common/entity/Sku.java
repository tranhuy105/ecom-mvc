package com.tranhuy105.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "skus")
@Getter
@Setter
public class Sku {
    public static final BigDecimal RATE_PER_CUBIC_METER = BigDecimal.valueOf(10);
    public static final BigDecimal RATE_PER_KG = BigDecimal.valueOf(5);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_code", length = 100, nullable = false)
    private String skuCode;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.valueOf(0);

    @Column(name = "discount_percent")
    private BigDecimal discountPercent = BigDecimal.valueOf(0);

    @Column(name = "stock_quantity",nullable = false)
    private Integer stockQuantity;

    // to calculate shipping fee
    private BigDecimal length = BigDecimal.valueOf(0);
    private BigDecimal width = BigDecimal.valueOf(0);
    private BigDecimal height = BigDecimal.valueOf(0);
    private BigDecimal weight = BigDecimal.valueOf(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public BigDecimal getDiscountedPrice() {
        if (price == null || discountPercent == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = price.multiply(discountPercent).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        return price.subtract(discountAmount);
    }

    public BigDecimal getShippingCost() {
        BigDecimal volume = length.multiply(width).multiply(height).divide(BigDecimal.valueOf(1_000_000), RoundingMode.HALF_UP);
        BigDecimal volumeCost = volume.multiply(RATE_PER_CUBIC_METER);
        BigDecimal weightCost = weight.multiply(RATE_PER_KG);
        return volumeCost.add(weightCost);
    }

}
