package com.tranhuy105.site.controller;

import com.tranhuy105.site.dto.AccountDTO;
import com.tranhuy105.site.dto.RegisterFormDTO;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.security.CustomerDetails;
import com.tranhuy105.site.security.CustomerOAuth2User;
import com.tranhuy105.site.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    @GetMapping("/customer")
    public String accountView(Model model, Authentication authentication) {
        AccountDTO accountDTO;
        if (authentication instanceof UsernamePasswordAuthenticationToken ||
        authentication instanceof RememberMeAuthenticationToken) {
            accountDTO = new AccountDTO(((CustomerDetails) authentication.getPrincipal()).getCustomer());
        } else if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            accountDTO = new AccountDTO(
                    customerService.findByEmail(
                            ((CustomerOAuth2User)oAuth2AuthenticationToken.getPrincipal()).getEmail()
                    )
            );
        } else {
            throw new NotFoundException("");
        }

        model.addAttribute("account", accountDTO);

        return "customer/account";
    }
}
