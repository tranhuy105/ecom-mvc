package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.dto.OrderDetailDTO;
import com.tranhuy105.site.payment.OrderService;
import com.tranhuy105.site.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;

    @GetMapping("/cod-success")
    public String showCodSuccessPage() {
        return "payment/cod-success";
    }

    @GetMapping("/me")
    public String myOrdersView(Model model,
                               Authentication authentication) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.findOrderByCustomerId(customer.getId()));


        return "customer/orders";
    }

    @GetMapping("/{orderNumber}")
    public String orderDetailView(@PathVariable String orderNumber,
                                  Model model,
                                  Authentication authentication) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            return "redirect:/login";
        }
        try {
            OrderDetailDTO dto = orderService.findOrderDetailByOrderNumber(customer.getId(), orderNumber);
            model.addAttribute("orderDetail", dto);
            model.addAttribute("pageTitle", dto.getOrderNumber());
            return "customer/order-detail";
        } catch (AccessDeniedException | IllegalArgumentException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("pageTitle", "Oops");
            return "message";
        }

    }

    @PostMapping("/{orderNumber}/cancel")
    public String cancelOrder(@PathVariable String orderNumber,
                              RedirectAttributes redirectAttributes,
                              Model model,
                              Authentication authentication){
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            return "redirect:/login";
        }
        try {
            orderService.cancelOrder(customer.getId(), orderNumber);
            redirectAttributes.addFlashAttribute("message", "Order Cancel");
            return "redirect:/orders/me";
        } catch (AccessDeniedException | IllegalArgumentException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("pageTitle", "Oops");
            return "message";
        }
    }
}
