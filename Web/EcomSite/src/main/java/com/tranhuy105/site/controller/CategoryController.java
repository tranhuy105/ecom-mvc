package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Category;
import com.tranhuy105.site.service.CategoryService;
import com.tranhuy105.site.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/categories")
    public String categoriesListingView(Model model) {
        model.addAttribute("listCategories", categoryService.findAllRoot());
        return "categories/categories";
    }

    @GetMapping("/categories/{alias}")
    public String categoryProductsView(Model model,
                                       @PathVariable String alias) {
        Category category = categoryService.findByAlias(alias);
        if (category == null) {
            model.addAttribute("status", 404);
            return "error";
        }
        List<Category> categoriesBreadcrumbTrail = categoryService.getBreadcrumbTrail(category);
        model.addAttribute("category", category);
        model.addAttribute("categoryParents", categoriesBreadcrumbTrail);
        model.addAttribute("productLists", productService.findTopProductsByCategory(category));
        model.addAttribute("pageTitle", category.getName());
        return "categories/category-products";
    }
}
