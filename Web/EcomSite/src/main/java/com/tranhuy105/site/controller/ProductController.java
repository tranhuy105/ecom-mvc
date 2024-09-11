package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Product;
import com.tranhuy105.common.util.PaginationUtil;
import com.tranhuy105.site.dto.ProductOverview;
import com.tranhuy105.site.dto.ReviewDTO;
import com.tranhuy105.site.dto.ReviewStatsDTO;
import com.tranhuy105.site.service.CategoryService;
import com.tranhuy105.site.service.ProductService;
import com.tranhuy105.site.service.ReviewService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @GetMapping("/products/{alias}")
    public String productDetailView(@PathVariable String alias,
                                    Model model) {
        Product product = productService.findByAlias(alias);
        model.addAttribute("product", product);
        if (product.getCategory() != null) {
            model.addAttribute("categoryParents", categoryService.getBreadcrumbTrail(product.getCategory()));
        }
        model.addAttribute("pageTitle", product.getName());


//        ReviewStatsDTO reviewStats = reviewService.getReviewStatsByProductId(product.getId());
        ReviewStatsDTO reviewStats = new ReviewStatsDTO(4.4, 102, 21, Arrays.asList(64, 22, 10, 2, 2));
        model.addAttribute("reviewStats", reviewStats);


        return "products/product-detail";
    }

    @GetMapping("/products")
    public String productListingView(
            Model model,
            @RequestParam(value = "page", required = false, defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "q", required = false) @Size(max = 100) String searchKeyword,
            @RequestParam(value = "c", required = false) Integer categoryId,
            @RequestParam(value = "b", required = false) Integer brandId,
            @RequestParam(value = "min_price", required = false) @DecimalMin("0.0") BigDecimal minPrice,
            @RequestParam(value = "max_price", required = false) @DecimalMin("0.0") BigDecimal maxPrice,
            @RequestParam(value = "sort", required = false, defaultValue = "name") @Pattern(regexp = "name|price|created_at") String sort,
            @RequestParam(value = "price_range", required = false) String priceRange,
            @RequestParam(value = "sort_direction", required = false, defaultValue = "asc") @Pattern(regexp = "asc|desc") String sortDirection
    ) {
        // Validation for minPrice <= maxPrice
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price.");
        }

        if (priceRange != null && !priceRange.isBlank()) {
            String[] range = priceRange.split("-");
            if (range.length != 2) {
                throw new IllegalArgumentException("Invalid price range");
            }
            try {
                minPrice = BigDecimal.valueOf(Integer.parseInt(range[0]));
                maxPrice = BigDecimal.valueOf(Integer.parseInt(range[1]));
            } catch (Exception exception) {
                throw new IllegalArgumentException("Invalid price range");
            }
        }

        Page<Product> productPage = productService.findMany(searchKeyword, categoryId, brandId, minPrice, maxPrice, page, sort, sortDirection);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("b", brandId);
        model.addAttribute("c", categoryId);
        model.addAttribute("products", productService.lazyFetchAttribute(productPage.getContent(), sort, sortDirection));
        model.addAttribute("brands", productService.findAllAvailableProductBrand());
        model.addAttribute("categories", categoryService.findAllRoot());
        model.addAttribute("priceRange", priceRange);
        PaginationUtil.setPaginationAttributes(page, productService.pageSize(), searchKeyword, model, productPage);
        return "products/products";
    }

    @GetMapping("/products/related")
    @ResponseBody
    public List<ProductOverview> getRelatedProducts(@RequestParam("id") Integer productId,
                                                    @RequestParam("page") Integer page) {
        return productService.getRelatedProducts(productId, PageRequest.of(page, 4));
    }

    @GetMapping("/products/by-brand")
    @ResponseBody
    public List<ProductOverview> getProductsByBrand(@RequestParam("id") Integer brandId,
                                            @RequestParam("page") Integer page) {
        return productService.getProductsByBrand(brandId, PageRequest.of(page, 4));
    }
}
