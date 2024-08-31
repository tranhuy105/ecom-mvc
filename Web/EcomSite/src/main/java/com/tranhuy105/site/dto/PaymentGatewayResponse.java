package com.tranhuy105.site.dto;

import lombok.Data;

@Data
public class PaymentGatewayResponse {
    private String transactionId;
    private String status;
    private String paymentMethod;
    private String orderNumber;
    private String gatewayResponse;
    private Integer amount;
    private boolean success;
}
