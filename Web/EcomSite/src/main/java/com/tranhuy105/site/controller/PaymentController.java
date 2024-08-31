package com.tranhuy105.site.controller;

import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.site.payment.PaymentService;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vnpay/callback")
    public String handleVNPayCallback(@RequestParam Map<String, String> params, Model model) {
        try {
            PaymentGatewayResponse response = paymentService.handlePaymentCallback(params, PaymentMethod.VNPAY);
            return handlePaymentResponse(response, model);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("pageTitle", "Payment Fail");
            return "message";
        }
    }

    @GetMapping("/paypal/callback")
    public String handleAnotherGatewayCallback(@RequestParam Map<String, String> params, Model model) {
        try {
            PaymentGatewayResponse response = paymentService.handlePaymentCallback(params, PaymentMethod.PAYPAL);
            return handlePaymentResponse(response, model);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("pageTitle", "Payment Fail");
            return "message";
        }
    }

    private String handlePaymentResponse(PaymentGatewayResponse response, Model model) {
        model.addAttribute("paymentResponse", response);
        if (response.isSuccess()) {
            return "payment/success";
        } else {
            return "payment/failure";
        }
    }
}
