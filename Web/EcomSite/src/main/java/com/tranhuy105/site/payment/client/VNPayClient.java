package com.tranhuy105.site.payment.client;

import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import com.tranhuy105.site.exception.PaymentException;
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
    @Value("${payment.vnpay.url}")
    private String VNPAY_URL;
    @Value("${payment.vnpay.return-url}")
    private String VNPAY_RETURN_URL;
    @Value("${payment.vnpay.tmn}")
    private String VNPAY_TMN_CODE;
    @Value("${payment.vnpay.secret}")
    private String VNPAY_HASH_SECRET;

    @Override
    public String createPaymentURL(Order order, String userIp, String transactionId) {
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", VNPAY_TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf(order.getFinalAmount().multiply(BigDecimal.valueOf(100)).intValue()));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", transactionId);
        vnpParams.put("vnp_OrderInfo", order.getOrderNumber());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", VNPAY_RETURN_URL);
        vnpParams.put("vnp_IpAddr", userIp);

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
        String vnpSecureHash = parameters.get("vnp_SecureHash");

        parameters.remove("vnp_SecureHash");

        // Generate the secure hash based on the incoming parameters (excluding the vnp_SecureHash itself)
        String hashData = generateQueryString(parameters, false);
        String generatedSecureHash = generateSecureHash(hashData, VNPAY_HASH_SECRET);

        // Verify the signature
        if (!vnpSecureHash.equals(generatedSecureHash)) {
            throw new PaymentException("Invalid VNPay callback signature");
        }

        // Continue processing if the signature is valid
        String status = parameters.get("vnp_ResponseCode");
        String transactionId = parameters.get("vnp_TransactionNo");
        String orderNumber = parameters.get("vnp_OrderInfo");
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
        response.setStatusCode(status);
        response.setStatusMessage(status != null ? getStatusMessage(status) : "N/A");
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

    private String getStatusMessage(String statusCode) {
        return switch (statusCode) {
            case "00" -> "Giao dịch thành công";
            case "07" -> "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09" ->
                    "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10" ->
                    "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11" ->
                    "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "12" -> "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
            case "13" ->
                    "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "24" -> "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51" ->
                    "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.";
            case "65" ->
                    "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
            case "75" -> "Ngân hàng thanh toán đang bảo trì.";
            case "79" ->
                    "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "99" -> "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)";
            default -> "Mã lỗi không xác định: " + statusCode;
        };
    }

}
