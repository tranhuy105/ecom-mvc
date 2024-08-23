package com.tranhuy105.admin.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranhuy105.admin.brand.BrandService;
import com.tranhuy105.admin.category.CategoryService;
import com.tranhuy105.admin.product.DTO.ProductDTO;
import com.tranhuy105.admin.product.DTO.ProductDetailDTO;
import com.tranhuy105.admin.product.DTO.ProductImageDTO;
import com.tranhuy105.admin.product.DTO.SkuDTO;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.common.entity.ProductDetail;
import com.tranhuy105.common.entity.ProductImage;
import com.tranhuy105.common.entity.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

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
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setEnabled(product.isEnabled());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setBrandId(product.getBrand() != null ? product.getBrand().getId() : null);
        dto.setAdditionalDetails(product.getAdditionalDetails().stream()
                .map(detail -> new ProductDetailDTO(detail.getId(), detail.getName(), detail.getValue()))
                .toList());
        dto.setImages(product.getImages().stream()
                .map(image -> new ProductImageDTO(image.getId(), image.getName()))
                .toList());
        dto.setSkus(product.getSkus().stream()
                .map(sku -> new SkuDTO(sku.getId(), sku.getSkuCode(), sku.getPrice(), sku.getDiscountPercent(), sku.getStockQuantity(),
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
        product.setCreatedAt(dto.getCreatedAt());
        product.setUpdatedAt(dto.getUpdatedAt());
        product.setEnabled(dto.isEnabled());
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
        if (ids.length != names.length || names.length != values.length) {
            throw new IllegalArgumentException("Arrays must be of the same length");
        }

        List<ProductDetailDTO> detailDTOList = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            ProductDetailDTO detailDTO = new ProductDetailDTO(ids[i], names[i], values[i]);
            detailDTOList.add(detailDTO);
        }
        return detailDTOList;
    }

    public List<ProductImageDTO> mapImageFromInstruction(String instructionJson) {
        List<ProductImageDTO> updatedImages = new ArrayList<>();

        try {
            List<Map<String, Object>> instructions = objectMapper.readValue(instructionJson, new TypeReference<>() {});

            for (Map<String, Object> instruction : instructions) {
                String instructionType = (String) instruction.get("instruction");
                Integer id = (Integer) instruction.get("id");
                String name = (String) instruction.get("name");

                if ("remove".equalsIgnoreCase(instructionType)) {
                    if (id != null) {
//                  productImageRepository.deleteById(id);
                    }
                } else if ("add".equalsIgnoreCase(instructionType) || "keep".equalsIgnoreCase(instructionType)) {
                    // Handle add or keep instructions
                    ProductImageDTO imageDTO = new ProductImageDTO();
                    imageDTO.setId(id);
                    imageDTO.setName(name);
                    updatedImages.add(imageDTO);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON string for instructions: " + instructionJson, e);
        }


        return updatedImages;
    }
}
