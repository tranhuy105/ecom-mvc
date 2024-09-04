package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.dto.CustomerDTO;
import com.tranhuy105.admin.dto.CustomerDetailDTO;
import com.tranhuy105.admin.service.CustomerService;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/customers")
    public String customerListingView(@RequestParam(required = false) String search,
                                      @RequestParam(required = false) Boolean enabled,
                                      @RequestParam(required = false, defaultValue = "1") int page,
                                      @RequestParam(required = false) String sortBy,
                                      @RequestParam(required = false) String sortDirection,
                                      Model model) {
        final int pageSize = 1;
        Page<CustomerDTO> customerPage = customerService.findAll(search, enabled, sortBy, sortDirection, page - 1, pageSize);
        model.addAttribute("customers", customerPage.getContent());
        PaginationUtil.setPaginationAttributes(page, pageSize, search, model, customerPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);
        return "customer/listing";
    }


    @GetMapping("/customers/new")
    public String createCustomerView(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/create";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomerView(@PathVariable Integer id, Model model) {
        CustomerDetailDTO customerDetail = customerService.getCustomerDetails(id);
        model.addAttribute("customer", customerDetail);
        return "customer/edit";
    }

    @PostMapping("/customers")
    public String createCustomer(Customer customer, RedirectAttributes redirectAttributes) {
        customerService.createCustomer(customer);
        redirectAttributes.addFlashAttribute("message", "Customer Create Success");
        return "redirect:/customers";
    }

    @PostMapping("/customers/edit/{id}")
    public String updateCustomer(@PathVariable Integer id, Model model) {
        return "redirect:/customers/edit/" + id;
    }

    @ResponseBody
    @PutMapping("/customers/{id}/enable")
    public ResponseEntity<String> enableCustomer(@PathVariable Integer id) {
        try {
            customerService.enableCustomer(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ResponseBody
    @PutMapping("/customers/{id}/disable")
    public ResponseEntity<String> disableCustomer(@PathVariable Integer id) {
        try {
            customerService.disableCustomer(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
