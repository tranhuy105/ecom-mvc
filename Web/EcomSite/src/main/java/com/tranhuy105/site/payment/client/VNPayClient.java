package com.tranhuy105.site.payment.client;

import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component("VNPAY")
@RequiredArgsConstructor
public class VNPayClient implements PaymentGatewayClient {
    private static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNPAY_RETURN_URL = "http://localhost:80/site/payment/vnpay/callback";
    @Value("${payment.vnpay.tmn}")
    private String VNPAY_TMN_CODE;
    @Value("${payment.vnpay.secret}")
    private String VNPAY_HASH_SECRET;

    @Override
    public String createPaymentURL(Order order, HttpServletRequest request) {
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", VNPAY_TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf(order.getFinalAmount().multiply(BigDecimal.valueOf(100)).intValue()));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", order.getOrderNumber());
        vnpParams.put("vnp_OrderInfo", "Order payment: " + order.getOrderNumber());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", VNPAY_RETURN_URL);
        vnpParams.put("vnp_IpAddr", request.getRemoteAddr());

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(calendar.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        String queryString = generateQueryString(vnpParams, true);
        String hashData = generateQueryString(vnpParams, false);
        String secureHash = generateSecureHash(hashData, VNPAY_HASH_SECRET);

        return VNPAY_URL + "?" + queryString + "&vnp_SecureHash=" + secureHash;
    }

    @Override
    public PaymentGatewayResponse parseCallback(Map<String, String> parameters) {
        String status = parameters.get("vnp_ResponseCode");
        String transactionId = parameters.get("vnp_TransactionNo");
        String orderNumber = parameters.get("vnp_TxnRef");
        String amount = parameters.get("vnp_Amount");
        Integer amountInt = null;
        try {
            amountInt = Integer.parseInt(amount);
            amountInt = amountInt / 100;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        PaymentGatewayResponse response = new PaymentGatewayResponse();
        response.setAmount(amountInt);
        response.setTransactionId(transactionId);
        response.setOrderNumber(orderNumber);
        response.setStatus(status);
        return response;
    }

    @Override
    public boolean isPaymentSuccessful(String status) {
        return "00".equals(status);
    }

    private String generateQueryString(Map<String, String> paramsMap, boolean encodeKey) {
        return paramsMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        (encodeKey ? URLEncoder.encode(entry.getKey(),
                                StandardCharsets.US_ASCII)
                                : entry.getKey()) + "=" +
                                URLEncoder.encode(entry.getValue()
                                        , StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }

    private String generateSecureHash(final String data, final String key) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
}
