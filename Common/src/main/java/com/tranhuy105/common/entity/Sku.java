package com.tranhuy105.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "skus")
@Getter
@Setter
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_code",unique = true, length = 100, nullable = false)
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
}
