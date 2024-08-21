package com.tranhuy105.admin.category;


import com.tranhuy105.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String viewCategoriesList(Model model) {
        model.addAttribute("listCategories", categoryService.findAll(1));
        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String createCategoryPage(Model model) {
        model.addAttribute("listCategories", categoryService.findAllWithHierarchy());
        model.addAttribute("category", new CategoryFormDTO());
        model.addAttribute("pageTitle", "Create New Category");
        return "categories/category_form";
    }

    @GetMapping("/categories/edit/{id}")
    public String updateCategoryPage(@PathVariable Integer id,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        Category category = categoryService.findById(id);
        if (category == null) {
            redirectAttributes.addFlashAttribute("message", "Couldn't found category with id "+id);
            return "redirect:/categories";
        }

        model.addAttribute("category", new CategoryFormDTO(category));
        model.addAttribute("listCategories", categoryService.findAllWithHierarchy());
        model.addAttribute("pageTitle", "Edit Category");
        return "categories/category_form";
    }

    @PostMapping("/categories")
    public String saveCategory(CategoryFormDTO categoryDTO,
                               @RequestParam(value = "file") MultipartFile file,
                               RedirectAttributes redirectAttributes
    ) {
        try {
            categoryService.updateCategoryInCache(categoryService.save(categoryDTO.toCategory(categoryService), file));
            redirectAttributes.addFlashAttribute("message", "Success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "IOException when saving category: " + e.getMessage());
        }
        return "redirect:/categories";
    }
}
