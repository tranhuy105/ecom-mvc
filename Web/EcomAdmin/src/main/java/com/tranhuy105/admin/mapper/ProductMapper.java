package com.tranhuy105.admin.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranhuy105.admin.dto.*;
import com.tranhuy105.admin.service.BrandService;
import com.tranhuy105.admin.service.CategoryService;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.common.entity.ProductDetail;
import com.tranhuy105.common.entity.ProductImage;
import com.tranhuy105.common.entity.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ObjectMapper objectMapper;

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setAlias(product.getAlias());
        dto.setShortDescription(product.getShortDescription());
        dto.setFullDescription(product.getFullDescription());
        if (product.getCreatedAt() != null) {
            dto.setCreatedAt(product.getCreatedAt().format(formatter));
        }
        if (product.getUpdatedAt() != null) {
            dto.setUpdatedAt(product.getUpdatedAt().format(formatter));
        }
        dto.setPrice(product.getPrice());
        dto.setDiscountPercent(product.getDiscountPercent());
        dto.setEnabled(product.isEnabled());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setBrandId(product.getBrand() != null ? product.getBrand().getId() : null);
        dto.setAdditionalDetails(product.getAdditionalDetails().stream()
                .map(detail -> new ProductDetailDTO(detail.getId(), detail.getName(), detail.getValue()))
                .toList());
        dto.setImages(product.getOrderedImages().stream()
                .map(image -> new ProductImageDTO(image.getId(), image.getName(), image.isMain()))
                .toList());
        dto.setSkus(product.getSkus().stream()
                .map(sku -> new SkuDTO(sku.getId(), sku.getSkuCode(), sku.getPrice(), sku.getCost(), sku.getDiscountPercent(), sku.getStockQuantity(),
                        sku.getLength(), sku.getWidth(), sku.getHeight(), sku.getWeight(), product.getId()))
                .toList());
        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setAlias(dto.getAlias());
        product.setShortDescription(dto.getShortDescription());
        product.setFullDescription(dto.getFullDescription());
        if (dto.getCreatedAt() != null) {
            product.setCreatedAt(LocalDateTime.parse(dto.getCreatedAt(), formatter));
        }
        if (dto.getUpdatedAt() != null) {
            product.setUpdatedAt(LocalDateTime.parse(dto.getUpdatedAt(), formatter));
        }
        product.setEnabled(dto.isEnabled());
        product.setDiscountPercent(dto.getDiscountPercent());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategoryId() != null ? categoryService.findById(dto.getCategoryId()) : null);
        product.setBrand(dto.getBrandId() != null ? brandService.findById(dto.getBrandId()) : null);

        product.setAdditionalDetails(dto.getAdditionalDetails().stream()
                .map(detailDTO -> {
                    ProductDetail detail = new ProductDetail();
                    detail.setId(detailDTO.getId());
                    detail.setName(detailDTO.getName());
                    detail.setValue(detailDTO.getValue());
                    detail.setProduct(product);
                    return detail;
                })
                .collect(Collectors.toSet()));

        product.setImages(dto.getImages().stream()
                .map(imageDTO -> {
                    ProductImage image = new ProductImage();
                    image.setId(imageDTO.getId());
                    image.setName(imageDTO.getName());
                    image.setProduct(product);
                    return image;
                })
                .collect(Collectors.toSet()));

        product.setSkus(dto.getSkus().stream()
                .map(skuDTO -> {
                    Sku sku = new Sku();
                    sku.setId(skuDTO.getId());
                    sku.setSkuCode(skuDTO.getSkuCode());
                    sku.setPrice(skuDTO.getPrice());
                    sku.setCost(skuDTO.getCost());
                    sku.setDiscountPercent(skuDTO.getDiscountPercent());
                    sku.setStockQuantity(skuDTO.getStockQuantity());
                    sku.setLength(skuDTO.getLength());
                    sku.setWidth(skuDTO.getWidth());
                    sku.setHeight(skuDTO.getHeight());
                    sku.setWeight(skuDTO.getWeight());
                    sku.setProduct(product);
                    return sku;
                })
                .collect(Collectors.toSet()));

        return product;
    }

    public List<SkuDTO> mapSkuDTOFromJson(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON string for skus: " + jsonString, e);
        }
    }

    public List<ProductDetailDTO> mapDetailDTO(Integer[] ids, String[] names, String[] values) {
        List<ProductDetailDTO> detailDTOList = new ArrayList<>();
        if (ids == null || names == null || values == null) {
            return detailDTOList;
        }
        if (ids.length != names.length || names.length != values.length) {
            throw new IllegalArgumentException("Arrays must be of the same length");
        }

        for (int i = 0; i < ids.length; i++) {
            ProductDetailDTO detailDTO = new ProductDetailDTO(ids[i], names[i], values[i]);
            detailDTOList.add(detailDTO);
        }
        return detailDTOList;
    }

    public List<ImageInstructionDTO> mapImageInstruction(String instructionJson) {
        try {
            return objectMapper.readValue(instructionJson, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON string for instructions: " + instructionJson, e);
        }
    }
}
