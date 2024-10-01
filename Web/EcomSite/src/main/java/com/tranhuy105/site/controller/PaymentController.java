package com.tranhuy105.site.controller;

import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.exception.PaymentRetryNotAllowedException;
import com.tranhuy105.site.payment.OrderService;
import com.tranhuy105.site.payment.PaymentService;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import com.tranhuy105.site.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CustomerService customerService;

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
//            PaymentGatewayResponse response = paymentService.parsePaymentResponse(params, PaymentMethod.VNPAY);
            PaymentGatewayResponse response = paymentService.handlePaymentCallback(params, PaymentMethod.VNPAY);

            return handlePaymentResponse(response, model);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("pageTitle", "Payment Fail");
            return "message";
        }
    }

    @PostMapping("/retry")
    public ResponseEntity<?> retryPayment(@RequestParam String orderNumber,
                                               @RequestParam PaymentMethod paymentMethod,
                                               Authentication authentication,
                                               HttpServletRequest request) {
        try {
            Order order = orderService.findOrderByOrderNumber(orderNumber);
            Customer customer = customerService.getCustomerFromAuthentication(authentication);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (order == null || !order.getCustomer().getId().equals(customer.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Order");
            }

            String paymentUrl = paymentService.initiatePayment(order, paymentMethod, request.getRemoteAddr());
            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));

        } catch (PaymentRetryNotAllowedException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment is not eligible for retry.");
        }
        catch (Exception e) {
            log.error("Error while retrying payment for order {}: {}", orderNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment retry failed");
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
