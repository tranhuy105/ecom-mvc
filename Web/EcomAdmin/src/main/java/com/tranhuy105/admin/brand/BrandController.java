package com.tranhuy105.admin.brand;

import com.tranhuy105.admin.utils.PaginationUtil;
import com.tranhuy105.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping("/brands")
    public String viewBrandListingPage(Model model,
                                       @RequestParam(value = "page", required = false) Integer page,
                                       @RequestParam(value = "q", required = false) String search) {
        page = PaginationUtil.sanitizePage(page);
        Page<Brand> listBrands =  brandService.findAll(page, search);
        List<BrandDTO> listBrandDTOs = listBrands.getContent().stream().map(BrandDTO::new).toList();
        PaginationUtil.setPaginationAttributes(page, BrandService.PAGE_SIZE,search, model, listBrands);
        model.addAttribute("listBrands", listBrandDTOs);
        return "brands/brands";
    }
}
