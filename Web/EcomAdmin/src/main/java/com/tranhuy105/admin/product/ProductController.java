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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductMapper productMapper;

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

    @GetMapping("/products/edit/{id}")
    public String editProductView(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ProductDTO productDTO = productMapper.toDTO(productService.findById(id));
        if (productDTO == null) {
            redirectAttributes.addFlashAttribute("message", "Couldn't find product with id "+id);
            return "redirect:/products";
        }
        model.addAttribute("productDTO", productDTO);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("listBrands", brandService.findAllMin());
        model.addAttribute("listCategories", categoryService.findAllWithHierarchy());
        return "products/product_form";
    }

    @GetMapping("/products/new")
    public String createProductView(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("listBrands", brandService.findAllMin());
        model.addAttribute("listCategories", categoryService.findAllWithHierarchy());
        return "products/product_form";
    }

    @PostMapping("/products")
    public String saveProduct(ProductDTO productDTO,
                              @RequestParam("imageList") MultipartFile[] imageFiles,
                              @RequestParam(value = "detailID", required = false) Integer[] detailIds,
                              @RequestParam(value = "detailName", required = false) String[] detailNames,
                              @RequestParam(value = "detailVal", required = false) String [] detailValues,
                              @RequestParam("skusJson") String skusJson,
                              @RequestParam("imageInstructions") String instructionJson) {
        productDTO.setSkus(productMapper.mapSkuDTOFromJson(skusJson));
        productDTO.setAdditionalDetails(productMapper.mapDetailDTO(detailIds, detailNames, detailValues));
        productDTO.setImages(productMapper.mapImageFromInstruction(instructionJson));
        System.out.println(productDTO);
        System.out.println("Received "+imageFiles.length+" new files.");
        for (MultipartFile file : imageFiles) {
            if (file.isEmpty()) {
                System.out.println("Empty file, skip");
                continue;
            }
            System.out.println("-----");
            System.out.println(file.getOriginalFilename());
        }
        return "redirect:/products";
    }
}
