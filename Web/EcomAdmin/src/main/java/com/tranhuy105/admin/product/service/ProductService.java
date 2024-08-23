package com.tranhuy105.admin.product.service;

import com.tranhuy105.admin.product.DTO.ImageInstructionDTO;
import com.tranhuy105.admin.product.repository.ProductRepository;
import com.tranhuy105.admin.utils.FileUploadUtil;
import com.tranhuy105.common.entity.Product;
import com.tranhuy105.common.entity.ProductDetail;
import com.tranhuy105.common.entity.ProductImage;
import com.tranhuy105.common.entity.Sku;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IProductService {
    public static final int PAGE_SIZE = 4;
    public static final String IMAGE_UPLOAD_DIR = "product-images";
    private final ProductRepository productRepository;

    @Override
    public Page<Product> findAll(int page, String search, Integer category) {
        return productRepository.findAll(PageRequest.of(page-1, PAGE_SIZE), search, category);
    }

    @Override
    public List<Product> lazyFetchAttribute(List<Product> products) {
        return productRepository.findAllFull(products);
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findByIdFull(id).orElse(null);
    }

    @Override
    public void save(Product product, MultipartFile[] _files, List<ImageInstructionDTO> imageInstructionDTOs) throws IOException {
        List<MultipartFile> files = Arrays.stream(_files).filter(file -> !file.isEmpty()).toList();
        List<ProductImage> newImageList = new ArrayList<>();
        List<ProductImage> deleteImageList = new ArrayList<>();
        List<ProductImage> keepImageList = new ArrayList<>();
        populateImageListBasedOnInstruction(newImageList, deleteImageList, keepImageList, imageInstructionDTOs, product);
        if (newImageList.size() != files.size()) {
            throw new IllegalArgumentException(String.format("Received %d new images but only %d files were provided", newImageList.size(), files.size()));
        }
        validateProduct(product);

        uploadNewProductImages(newImageList, files);
        deleteProductImages(deleteImageList);

        Set<ProductImage> combinedImages = new HashSet<>(keepImageList);
        combinedImages.addAll(newImageList);

        product.setImages(combinedImages);
        productRepository.save(product);
    }

    private void uploadNewProductImages(List<ProductImage> newImageList, List<MultipartFile> files) throws IOException {
        for (int i = 0; i < newImageList.size(); i++) {
            ProductImage image = newImageList.get(i);
            MultipartFile file = files.get(i);
            String fileName = FileUploadUtil.validateAndGetImageFilename(file);
            FileUploadUtil.saveFile(IMAGE_UPLOAD_DIR, fileName, file);
            image.setName(fileName);
        }
    }

    private void deleteProductImages(List<ProductImage> deleteImageList) {
        for (ProductImage image : deleteImageList) {
            FileUploadUtil.deleteFile(IMAGE_UPLOAD_DIR, image.getName());
        }
    }


    private void validateProduct(Product product) {
        boolean isUpdate = product.getId() != null;

        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }

        if (isUpdate) {
            if (product.getAlias() == null || product.getAlias().isBlank()) {
                throw new IllegalArgumentException("Product alias can't be blank");
            }
        } else {
            if (product.getAlias() == null || product.getAlias().isBlank()) {
                String alias = getAliasByName(product.getName());
                boolean isDup = productRepository.findByAliasMin(alias).isPresent();
                if (isDup) {
                    throw new IllegalArgumentException("this alias has already been occupied");
                }
            } else {
                validateAliasFormat(product.getAlias());
                boolean isDup = productRepository.findByAliasMin(product.getAlias()).isPresent();
                if (isDup) {
                    throw new IllegalArgumentException("this alias has already been occupied");
                }
            }
        }

        if (product.getSkus() == null || product.getSkus().isEmpty()) {
            throw new IllegalArgumentException("Product must contain at least 1 SKU");
        }

        if (product.getShortDescription() == null || product.getShortDescription().isBlank()) {
            throw new IllegalArgumentException("Short description cannot be blank");
        }
        if (product.getFullDescription() == null || product.getFullDescription().isBlank()) {
            throw new IllegalArgumentException("Full description cannot be blank");
        }

        if (product.getCategory() != null && product.getCategory().getId() == null) {
            throw new IllegalArgumentException("Category must be a valid reference if provided");
        }
        if (product.getBrand() != null && product.getBrand().getId() == null) {
            throw new IllegalArgumentException("Brand must be a valid reference if provided");
        }
        validateSku(product.getSkus());
    }


    private void validateSku(Set<Sku> skus) {
        if (skus == null || skus.isEmpty()) {
            throw new IllegalArgumentException("Product must contain at least one SKU");
        }

        Set<String> skuCodes = new HashSet<>();
        for (Sku sku : skus) {
            if (sku.getSkuCode() == null || sku.getSkuCode().isBlank()) {
                throw new IllegalArgumentException("SKU code cannot be blank");
            }
            if (!skuCodes.add(sku.getSkuCode())) {
                throw new IllegalArgumentException("SKU code must be unique for an product");
            }

            if (sku.getPrice() == null || sku.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("SKU price must be non-negative");
            }

            if (sku.getDiscountPercent() != null && (sku.getDiscountPercent().compareTo(BigDecimal.ZERO) < 0 ||
                    sku.getDiscountPercent().compareTo(BigDecimal.valueOf(100)) > 0)) {
                throw new IllegalArgumentException("SKU discount percent must be between 0 and 100");
            }

            if (sku.getStockQuantity() == null || sku.getStockQuantity() < 0) {
                throw new IllegalArgumentException("SKU stock quantity must be non-negative");
            }

            if (sku.getLength() == null || sku.getLength().compareTo(BigDecimal.ZERO) < 0 ||
                    sku.getWidth() == null || sku.getWidth().compareTo(BigDecimal.ZERO) < 0 ||
                    sku.getHeight() == null || sku.getHeight().compareTo(BigDecimal.ZERO) < 0 ||
                    sku.getWeight() == null || sku.getWeight().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("SKU dimensions and weight must be non-negative");
            }
        }
    }


    private void populateImageListBasedOnInstruction(List<ProductImage> newImageList,
                                                     List<ProductImage> deleteImageList,
                                                     List<ProductImage> keepImageList,
                                                     List<ImageInstructionDTO> imageInstructionDTOs,
                                                     Product product) {
        boolean isUpdateProduct = product.getId()!=null;
        if (isUpdateProduct) {
            imageInstructionDTOs.forEach(imageInstructionDTO -> {
                String instruction = imageInstructionDTO.getInstruction();
                if (instruction == null) {
                    throw new IllegalArgumentException("NULL instruction.");
                }
                switch (instruction.toLowerCase()) {
                    case "remove" -> deleteImageList.add(new ProductImage(imageInstructionDTO.getId(), imageInstructionDTO.getName(), product));
                    case "add" -> newImageList.add(new ProductImage(imageInstructionDTO.getId(), imageInstructionDTO.getName(), product));
                    case "keep" -> keepImageList.add(new ProductImage(imageInstructionDTO.getId(), imageInstructionDTO.getName(), product));
                    default -> throw new IllegalArgumentException("Illegal Instruction: "+imageInstructionDTO.getInstruction());
                }
            });
        } else {
            imageInstructionDTOs.forEach(imageInstructionDTO -> newImageList.add(new ProductImage(imageInstructionDTO.getId(), imageInstructionDTO.getName(), product)));
        }
    }

    @Override
    public void delete(Integer id) {
        List<String> removedImages = productRepository.findAllProductImageNames(id);
        productRepository.delete(id);
        removedImages.forEach(removedImage -> {
            try {
                FileUploadUtil.deleteFile(IMAGE_UPLOAD_DIR, removedImage);
            } catch (Exception exception) {
                log.warn("FAIL TO CLEAN UP IMAGE WHEN DELETING PRODUCT: "+removedImage+"\n"+exception.getMessage());
            }
        });
    }

    private String getAliasByName(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String asciiOnly = normalized.replaceAll("\\p{M}", "");
        String lowerCase = asciiOnly.toLowerCase();

        // Replace spaces with hyphens
        String withHyphens = lowerCase.replaceAll("\\s+", "-");

        // Remove characters that are not alphanumeric or hyphens
        String sanitized = withHyphens.replaceAll("[^a-z0-9-]", "");

        // Ensure alias is not empty
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Can not create a default alias for name: "+name+", consider add an alias manually.");
        }

        return sanitized;
    }

    private void validateAliasFormat(String alias) {
        String aliasPattern = "^[a-zA-Z0-9-]{3,50}$";
        if (!alias.matches(aliasPattern)) {
            throw new IllegalArgumentException("Alias contains invalid characters or does not meet length requirements (3 - 50 characters).");
        }
    }

    public Product getMockProduct() {
        Product product = new Product();
        product.setName("OreGairu Book Vol 14.5");
        product.setShortDescription("A limited edition volume of OreGairu featuring special content.");
        product.setFullDescription("""
                Each person's experience with youth is unique, and two girls in particular have a very different view: the most powerful junior and cutest troublemaker Iroha Isshiki, and everyone's little sister Komachi Hikigaya. Will the two eccentric younger heroines provide a breath of fresh air to the Service Club, or is the club's future in peril? Get ready for a changing of the guard!

                This volume contains short stories not included in the collected volumes, as well as a totally new story!!""");
        product.setAlias("oregairu-vol-14-5");

        Set<Sku> skus = new HashSet<>();

        Sku sku1 = new Sku();
        sku1.setSkuCode("OREGAIRU-14.5-001");
        sku1.setPrice(BigDecimal.valueOf(29.99));
        sku1.setDiscountPercent(BigDecimal.valueOf(10));
        sku1.setStockQuantity(100);
        sku1.setLength(BigDecimal.valueOf(20));
        sku1.setWidth(BigDecimal.valueOf(15));
        sku1.setHeight(BigDecimal.valueOf(2));
        sku1.setWeight(BigDecimal.valueOf(0.5));

        Sku sku2 = new Sku();
        sku2.setSkuCode("OREGAIRU-14.5-002");
        sku2.setPrice(BigDecimal.valueOf(34.99));
        sku2.setDiscountPercent(BigDecimal.valueOf(5));
        sku2.setStockQuantity(50);
        sku2.setLength(BigDecimal.valueOf(20));
        sku2.setWidth(BigDecimal.valueOf(15));
        sku2.setHeight(BigDecimal.valueOf(2));
        sku2.setWeight(BigDecimal.valueOf(0.5));

        skus.add(sku1);
        skus.add(sku2);
        product.setSkus(skus);

        ProductDetail detail1 = new ProductDetail();
        detail1.setName("ISBN");
        detail1.setValue("978-1234567890");
        detail1.setProduct(product);

        ProductDetail detail2 = new ProductDetail();
        detail2.setName("Publisher");
        detail2.setValue("Yen Press");
        detail2.setProduct(product);

        ProductDetail detail3 = new ProductDetail();
        detail3.setName("Author");
        detail3.setValue("Wataru Watari");
        detail3.setProduct(product);

        ProductDetail detail4 = new ProductDetail();
        detail4.setName("Language");
        detail4.setValue("English");
        detail4.setProduct(product);

        ProductDetail detailT = new ProductDetail();
        detailT.setName("Translator");
        detailT.setValue("Jennifer Ward");
        detailT.setProduct(product);

        ProductDetail detail5 = new ProductDetail();
        detail5.setName("Release Date");
        detail5.setValue("2024-05-15");
        detail5.setProduct(product);

        ProductDetail detail6 = new ProductDetail();
        detail6.setName("Pages");
        detail6.setValue("200");
        detail6.setProduct(product);

        ProductDetail detail7 = new ProductDetail();
        detail7.setName("Dimensions");
        detail7.setValue("20 x 15 x 2 cm");
        detail7.setProduct(product);

        ProductDetail detail8 = new ProductDetail();
        detail8.setName("Weight");
        detail8.setValue("0.5 kg");
        detail8.setProduct(product);

        Set<ProductDetail> productDetails = new HashSet<>(List.of(detail1, detail2, detail3, detail4, detailT, detail5, detail6, detail7, detail8));
        product.setAdditionalDetails(productDetails);

        return product;
    }
}
