package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Product;
import com.tranhuy105.common.util.PaginationUtil;
import com.tranhuy105.site.service.CategoryService;
import com.tranhuy105.site.service.ProductService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/products/{alias}")
    public String productDetailView(@PathVariable String alias,
                                    Model model) {
        Product product = productService.findByAlias(alias);
        model.addAttribute("product", product);
        model.addAttribute("categoryParents", categoryService.getBreadcrumbTrail(product.getCategory()));
        model.addAttribute("pageTitle", product.getName());
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
            @RequestParam(value = "sort_direction", required = false, defaultValue = "asc") @Pattern(regexp = "asc|desc") String sortDirection
    ) {
        // Validation for minPrice <= maxPrice
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price.");
        }

        Page<Product> productPage = productService.findMany(searchKeyword, categoryId, brandId, minPrice, maxPrice, page, sort, sortDirection);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("b", brandId);
        model.addAttribute("products", productService.lazyFetchAttribute(productPage.getContent(), sort, sortDirection));
        model.addAttribute("brands", productService.findAllAvailableProductBrand());
        PaginationUtil.setPaginationAttributes(page, productService.pageSize(), searchKeyword, model, productPage);
        return "products/products";
    }
}