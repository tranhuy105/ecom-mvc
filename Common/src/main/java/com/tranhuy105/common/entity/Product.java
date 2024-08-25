package com.tranhuy105.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_alias", columnList = "alias"),
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_category_id", columnList = "category_id"),
        @Index(name = "idx_brand_id", columnList = "brand_id")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String alias;
    @Column(name = "short_description" ,length = 512, nullable = false)
    private String shortDescription;
    @Column(name = "full_description",length = 4096, nullable = false)
    private String fullDescription;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private boolean enabled = true;
    @Column(name = "default_price")
    private BigDecimal price = BigDecimal.valueOf(0);
    @Column(name = "default_discount")
    private BigDecimal discountPercent = BigDecimal.valueOf(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductDetail> additionalDetails = new HashSet<>();

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Sku> skus = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BigDecimal getDiscountedPrice() {
        if (price == null || discountPercent == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = price.multiply(discountPercent).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        return price.subtract(discountAmount);
    }

    public List<Sku> getAvailableSkus() {
        return this.skus.stream().sorted(Comparator.comparing(Sku::getSkuCode)).toList();
    }

    public List<ProductImage> getOrderedImages() {
        return this.images.stream().sorted(Comparator.comparing(ProductImage::getId)).toList();
    }
}
