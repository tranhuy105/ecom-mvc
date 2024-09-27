package com.tranhuy105.site.controller;

import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.site.payment.PaymentService;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vnpay/webhook")
    @ResponseBody
    public ResponseEntity<String> handleVNPayWebhook(@RequestParam Map<String, String> params) {
        try {
            PaymentGatewayResponse response = paymentService.handlePaymentCallback(params, PaymentMethod.VNPAY);

            if (response.isSuccess()) {
                return ResponseEntity.ok("Webhook processed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed or invalid");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }


    @GetMapping("/vnpay/callback")
    public String handleVNPayCallback(@RequestParam Map<String, String> params, Model model) {
        try {
            PaymentGatewayResponse response = paymentService.parsePaymentResponse(params, PaymentMethod.VNPAY);
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
