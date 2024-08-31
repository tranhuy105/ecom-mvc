package com.tranhuy105.site.payment.client;

import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentGatewayClient {
    String createPaymentURL(Order order, HttpServletRequest request);
    PaymentGatewayResponse parseCallback(Map<String, String> parameters);
    boolean isPaymentSuccessful(String status);
}