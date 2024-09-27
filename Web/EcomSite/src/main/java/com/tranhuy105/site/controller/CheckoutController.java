package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.*;
import com.tranhuy105.site.exception.PaymentException;
import com.tranhuy105.site.payment.OrderService;
import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.site.payment.PaymentService;
import com.tranhuy105.site.service.AddressService;
import com.tranhuy105.site.service.CustomerService;
import com.tranhuy105.site.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;

@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
@Slf4j
public class CheckoutController {
    private final CustomerService customerService;
    private final ShoppingCartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final AddressService addressService;

    @GetMapping("/review")
    public String reviewOrder(Model model, Authentication authentication) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            model.addAttribute("pageTitle", "Unauthorized");
            model.addAttribute("message", "please log in to see your order detail");
            return "message";
        }
        Address address = addressService.findMainAddressForCustomer(customer);
        ShoppingCart cart = cartService.getOrCreateCartForCustomer(customer.getId());
        model.addAttribute("cartItems", cart.getCartItems().stream().sorted(Comparator.comparing(CartItem::getId)).toList());
        model.addAttribute("address", address == null ? "" : address.toString());
        model.addAttribute("shippingAddressId", address != null ? address.getId() : null);
        return "payment/checkout";
    }

    @PostMapping("/initiate")
    public String placeOrder(@RequestParam("shippingAddressId") Integer shippingAddressId,
                             @RequestParam("paymentMethod") String paymentMethod,
                             Authentication authentication,
                             Model model,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        PaymentMethod selectedPaymentMethod = PaymentMethod.valueOf(paymentMethod);

        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            model.addAttribute("pageTitle", "Unauthorized");
            model.addAttribute("message", "please log in to see your order detail");
            return "message";
        }

        if (!PaymentMethod.COD.equals(selectedPaymentMethod)) {
            Order order = orderService.createOrder(customer.getId(), shippingAddressId);
            if (order == null) {
                model.addAttribute("pageTitle", "Order Not Found.");
                model.addAttribute("message", "System can not process your order confirmation request.");
                return "message";
            }
            try {
                String paymentUrl = paymentService.initiatePayment(order, selectedPaymentMethod, request);
                return "redirect:" + paymentUrl;
            } catch (PaymentException | IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("message", e.getMessage());
                return "redirect:/checkout/review";
            }
        } else {
            try {
                Order codOrder = orderService.createCodOrder(customer.getId(), shippingAddressId);
                redirectAttributes.addFlashAttribute("order", codOrder);
                redirectAttributes.addFlashAttribute("customer", customer);
                return "redirect:/orders/cod-success";
            } catch (IllegalArgumentException exception) {
                redirectAttributes.addFlashAttribute("message", exception.getMessage());
                return "redirect:/checkout/review";
            } catch (Exception exception) {
                log.error("Lỗi xảy ra khi tạo COD order", exception);
                redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra khi xử lí yêu cầu của bạn. vui lòng thử lại sau.");
                return "redirect:/checkout/review";
            }
        }
    }
}
