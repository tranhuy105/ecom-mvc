package com.tranhuy105.admin.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranhuy105.admin.brand.BrandService;
import com.tranhuy105.admin.category.CategoryService;
import com.tranhuy105.admin.product.DTO.ProductDTO;
import com.tranhuy105.admin.product.service.ProductService;
import com.tranhuy105.admin.utils.PaginationUtil;
import com.tranhuy105.common.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    @GetMapping("/products")
    public String productListingView(Model model,
                                     @RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "q", required = false) String search,
                                     @RequestParam(value = "category", required = false) Integer categoryId) {
        page = PaginationUtil.sanitizePage(page);
        if (search != null) {
            if (search.isEmpty() || search.isBlank()) {
                search = null;
            }
        }
        if (categoryId != null) {
            if (categoryId.equals(0)) {
                categoryId = null;
            }
        }
        Page<Product> productPage = productService.findAll(page, search, categoryId);
        PaginationUtil.setPaginationAttributes(page, ProductService.PAGE_SIZE, search, model, productPage);
        model.addAttribute("listProducts", productService.lazyFetchAttribute(productPage.getContent()));
        model.addAttribute("listCategories", categoryService.findAllWithHierarchy());
        if (categoryId != null) {
            model.addAttribute("categoryId", categoryId);
        }
        return "products/products";
    }

    @GetMapping("/products/new")
    public String createProductView(Model model) {
        ProductDTO productDTO = productMapper.toDTO(productService.findById(1));
        System.out.println(productDTO.getShortDescription());
        model.addAttribute("productDTO", productDTO);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("listBrands", brandService.findAllMin());
        model.addAttribute("listCategories", categoryService.findAllWithHierarchy());
        return "products/product_form";
    }

    @PostMapping("/products")
    public String saveProduct(ProductDTO productDTO,
                              @RequestParam("skusJson") String skusJson,
                              @RequestParam("detailName") String[] detailNames,
                              @RequestParam("detailVal") String[] detailValues) {
        productDTO.setSkusFromJson(skusJson, objectMapper);
        productDTO.getSkus().forEach(System.out::println);
        System.out.println(productDTO);
        Arrays.stream(detailNames).forEach(System.out::println);
        System.out.println("---");
        Arrays.stream(detailValues).forEach(System.out::println);
        return "redirect:/products";
    }
}
