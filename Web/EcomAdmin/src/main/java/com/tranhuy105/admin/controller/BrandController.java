package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.dto.BrandDTO;
import com.tranhuy105.admin.service.BrandService;
import com.tranhuy105.admin.service.CategoryService;
import com.tranhuy105.common.util.PaginationUtil;
import com.tranhuy105.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;
    private final CategoryService categoryService;

    @GetMapping("/brands")
    public String viewBrandListingPage(Model model,
                                       @RequestParam(value = "page", required = false) Integer page,
                                       @RequestParam(value = "q", required = false) String search) {
        page = PaginationUtil.sanitizePage(page);
        Page<Brand> listBrands =  brandService.findAll(page, search);
        List<BrandDTO> listBrandDTOs = listBrands.getContent().stream().map(BrandDTO::new).toList();
        PaginationUtil.setPaginationAttributes(page, brandService.getPageSize() ,search, model, listBrands);
        model.addAttribute("listBrands", listBrandDTOs);
        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String createBrandView(Model model) {
        model.addAttribute("listCategories", categoryService.findAllWithHierarchy());
        model.addAttribute("brand", new BrandDTO());
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrandView(@PathVariable Integer id, RedirectAttributes redirectAttributes, Model model) {
        Brand brand = brandService.findById(id);

        if (brand == null) {
            redirectAttributes.addFlashAttribute("message", "Couldn't found brand with id "+id);
            return "redirect:/categories";
        }

        model.addAttribute("brand", new BrandDTO(brand));
        model.addAttribute("listCategories", categoryService.findAllWithHierarchy());
        model.addAttribute("pageTitle", "Edit Brand");
        return "brands/brand_form";
    }

    @PostMapping("/brands")
    public String saveBrand(BrandDTO brandDTO,
                            RedirectAttributes redirectAttributes
    ) {
        try {
            brandService.save(brandDTO.toBrand(categoryService));
            redirectAttributes.addFlashAttribute("message", "Success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/brands";
    }

    @PostMapping("/brands/{id}")
    public String deleteBrand(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        brandService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Category deleted");
        return "redirect:/brands";
    }
}
