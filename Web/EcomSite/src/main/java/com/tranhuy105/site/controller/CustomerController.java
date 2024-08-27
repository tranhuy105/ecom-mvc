package com.tranhuy105.site.controller;

import com.tranhuy105.site.dto.RegisterFormDTO;
import com.tranhuy105.site.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/register")
    public String customerRegisterView(Model model) {
        model.addAttribute("customer", new RegisterFormDTO());
        model.addAttribute("countriesList", customerService.findAvailableCountry());
        model.addAttribute("pageTitle", "Customer Registration");

        return "register/register-form";
    }

    @PostMapping("/register")
    public String registerCustomer(Model model, RegisterFormDTO dto) {
        try {
            customerService.registerCustomer(dto);
            model.addAttribute("message", "You Have Successfully Register As A Customer.");
        } catch (Exception exception) {
            exception.printStackTrace();
            model.addAttribute("error", exception.getMessage());
        }

        return "register/register-success";
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model) {
        try {
            customerService.verifyAccount(code);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
        } catch (Exception exception) {
            model.addAttribute("error", "Oops, Something went wrong");
        }

        return "register/verify";
    }
}
