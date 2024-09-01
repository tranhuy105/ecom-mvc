package com.tranhuy105.admin.service;

import com.tranhuy105.admin.dto.ghn.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GhnApiService {

    @Value("${ghn.api.token}")
    private String token;

    @Value("${ghn.api.shopId}")
    private String shopId;

    @Value("${ghn.api.url}")
    private String ghnApiUrl;

    private final RestTemplate restTemplate;

    public GhnApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GhnOrderResponse createOrder(GhnOrderRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Token", token);
        headers.set("ShopId", shopId);

        HttpEntity<GhnOrderRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GhnOrderResponse> response = restTemplate.exchange(
                ghnApiUrl + "/shipping-order/create",
                HttpMethod.POST,
                entity,
                GhnOrderResponse.class
        );

        return response.getBody();
    }

    public GhnTokenResponse generatePrintOrderToken(List<String> orderCodes) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Token", token);

        GhnPrintOrderRequest request = new GhnPrintOrderRequest();
        request.setOrderCodes(orderCodes);

        HttpEntity<GhnPrintOrderRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GhnTokenResponse> response = restTemplate.exchange(
                ghnApiUrl + "/a5/gen-token",
                HttpMethod.POST,
                entity,
                GhnTokenResponse.class
        );

        return response.getBody();
    }

    public GhnDistrictResponse getDistricts(Integer provinceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Token", token);

        GhnDistrictRequest request = new GhnDistrictRequest();
        request.setProvinceId(provinceId);

        HttpEntity<GhnDistrictRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GhnDistrictResponse> response = restTemplate.exchange(
                "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/district",
                HttpMethod.POST,
                entity,
                GhnDistrictResponse.class
        );

        return response.getBody();
    }

    public GhnWardResponse getWards(Integer districtId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Token", token);

        GhnWardRequest request = new GhnWardRequest();
        request.setDistrictId(districtId);

        HttpEntity<GhnWardRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GhnWardResponse> response = restTemplate.exchange(
                "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_id=" + districtId,
                HttpMethod.GET,
                entity,
                GhnWardResponse.class
        );

        return response.getBody();
    }

    public GhnProvinceResponse getProvinces() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Token", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GhnProvinceResponse> response = restTemplate.exchange(
                "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/province",
                HttpMethod.GET,
                entity,
                GhnProvinceResponse.class
        );

        return response.getBody();
    }
}
