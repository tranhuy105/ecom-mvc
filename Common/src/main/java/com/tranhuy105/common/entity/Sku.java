package com.tranhuy105.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "skus")
@Getter
@Setter
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_code", length = 100, nullable = false)
    private String skuCode;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.valueOf(0);

    @Column(nullable = false)
    private BigDecimal cost = BigDecimal.valueOf(0);

    @Column(name = "discount_percent")
    private BigDecimal discountPercent = BigDecimal.valueOf(0);

    @Column(name = "sale_start")
    private LocalDateTime saleStart;
    @Column(name = "sale_end")
    private LocalDateTime saleEnd;

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

    public boolean isOnSale() {
        LocalDateTime now = LocalDateTime.now();
        return saleStart != null
                && saleEnd != null
                && now.isAfter(saleStart)
                && now.isBefore(saleEnd)
                && discountPercent.compareTo(BigDecimal.ZERO) > 0;
    }


    public BigDecimal getShippingCost() {
       return BigDecimal.ZERO;
    }
}
