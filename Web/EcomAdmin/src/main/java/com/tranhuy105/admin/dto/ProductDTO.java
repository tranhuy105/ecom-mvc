package com.tranhuy105.admin.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private String createdAt;
    private String updatedAt;
    private BigDecimal rating;
    private Integer reviewsCount;
    private boolean enabled = true;
    private Integer categoryId;
    private Integer brandId;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private List<ProductDetailDTO> additionalDetails = new ArrayList<>();
    private List<ProductImageDTO> images = new ArrayList<>();
    private List<SkuDTO> skus = new ArrayList<>();

    public String getSkusJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        boolean first = true;
        for (SkuDTO sku : skus) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            sb.append("{")
                    .append("\"id\":").append(sku.getId()).append(",")
                    .append("\"skuCode\":\"").append(sku.getSkuCode()).append("\",")
                    .append("\"price\":").append(sku.getPrice()).append(",")
                    .append("\"cost\":").append(sku.getCost()).append(",")
                    .append("\"discountPercent\":").append(sku.getDiscountPercent()).append(",")
                    .append("\"stockQuantity\":").append(sku.getStockQuantity()).append(",")
                    .append("\"length\":").append(sku.getLength()).append(",")
                    .append("\"width\":").append(sku.getWidth()).append(",")
                    .append("\"height\":").append(sku.getHeight()).append(",")
                    .append("\"weight\":").append(sku.getWeight()).append(",");

            // Handle saleStart and saleEnd (null-safe)
            sb.append("\"saleStart\":");
            if (sku.getSaleStart() != null) {
                sb.append("\"").append(sku.getSaleStart().format(formatter)).append("\"");
            } else {
                sb.append("null");
            }

            sb.append(",");

            sb.append("\"saleEnd\":");
            if (sku.getSaleEnd() != null) {
                sb.append("\"").append(sku.getSaleEnd().format(formatter)).append("\"");
            } else {
                sb.append("null");
            }

            sb.append(",")
                    .append("\"productId\":").append(sku.getProductId())
                    .append("}");
        }

        sb.append("]");
        return sb.toString();
    }
}
