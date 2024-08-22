package com.tranhuy105.admin.product.DTO;

import com.tranhuy105.admin.brand.BrandService;
import com.tranhuy105.admin.category.CategoryService;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.common.entity.ProductDetail;
import com.tranhuy105.common.entity.ProductImage;
import com.tranhuy105.common.entity.Sku;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductDTO {
    private Integer id;
    private String name;
    private String alias;
    private String shortDescription;
    private String fullDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled = true;
    private Integer categoryId;
    private Integer brandId;
    private Set<ProductDetailDTO> additionalDetails;
    private Set<ProductImageDTO> images;
    private Set<SkuDTO> skus;
}
