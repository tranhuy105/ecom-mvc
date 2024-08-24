package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Product;
import com.tranhuy105.site.service.CategoryService;
import com.tranhuy105.site.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
