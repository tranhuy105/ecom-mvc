package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Address;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.entity.ShoppingCart;
import com.tranhuy105.site.dto.AccountDTO;
import com.tranhuy105.site.dto.RegisterFormDTO;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.security.CustomerDetails;
import com.tranhuy105.site.security.CustomerOAuth2User;
import com.tranhuy105.site.service.AddressService;
import com.tranhuy105.site.service.CustomerService;
import com.tranhuy105.site.service.ShoppingCartService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final ShoppingCartService shoppingCartService;

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

    @GetMapping("/account/forgot")
    public String forgotPasswordView() {
        return "customer/forgot-password";
    }

    @PostMapping("/account/forgot")
    public String submitForgotPasswordEmail(String email, RedirectAttributes redirectAttributes) {
        try {
            customerService.requestToResetPassword(email);
            redirectAttributes.addFlashAttribute("message", "An reset password link has been sent to your email.");
        } catch (NotFoundException exception) {
            redirectAttributes.addFlashAttribute("error", "Couldn't found any account associated with this email.");
        }

        return "redirect:/account/forgot";
    }

    @GetMapping("/account/reset")
    public String resetPasswordView(@RequestParam("code") String code, Model model) {
        if (code.length() != 32) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("title", "Invalid Token");
            return "message";
        }

        Customer customer = customerService.findByResetPasswordCode(code);

        if (customer != null) {
            model.addAttribute("code", code);
        } else {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("title", "Invalid Token");
            return "message";
        }

        return "customer/reset-password";
    }

    @PostMapping("/account/reset")
    public String resetPassword(@RequestParam("code") String resetCode,
                                @RequestParam("password") String newPassword,
                                Model model) {
        model.addAttribute("pageTitle", "Reset Password");

        try {
            customerService.resetPassword(resetCode, newPassword);
            model.addAttribute("message", "Your password has been reset. Please try to log in again");
        } catch (NotFoundException exception) {
            model.addAttribute("title", "Invalid Token");
            model.addAttribute("message", "This could occurred if the token is wrong or it has already been used.");
        } catch (Exception exception) {
            model.addAttribute("title", "Operation Fail");
            model.addAttribute("message", "Can not reset your password, please try again.");
        }

        return "message";
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

    @PostMapping("/customer")
    public String updateAccount(AccountDTO accountDTO,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication) {
        Customer customer = customerService.update(accountDTO);
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");

        if (authentication instanceof UsernamePasswordAuthenticationToken ||
                authentication instanceof RememberMeAuthenticationToken) {
            CustomerDetails customerDetails = ((CustomerDetails) authentication.getPrincipal());
            if (customerDetails != null) {
                customerDetails.setCustomer(customer);
            }
        } else if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            oauth2User.setFullName(fullName);
        } else {
            throw new NotFoundException("");
        }

        return "redirect:/customer";
    }

    @GetMapping("/customer/cart")
    public String customerCartView(Authentication authentication, Model model) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            model.addAttribute("pageTitle");
            model.addAttribute("message", "please log in to see your cart");
            return "message";
        }

        ShoppingCart cart = shoppingCartService.getOrCreateCartForCustomer(customer.getId());
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("pageTitle", "Shopping Cart");
        BigDecimal total =  shoppingCartService.calculateTotalPrice(cart);
        BigDecimal subTotal = shoppingCartService.calculateSubtotalPrice(cart);
        model.addAttribute("total", total);
        model.addAttribute("subTotal", subTotal);
        model.addAttribute("discount", subTotal.subtract(total));
        return "cart/cart";
    }
}
