package com.tranhuy105.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductDetail> additionalDetails = new ArrayList<>();

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductImage> images = new ArrayList<>();
}
