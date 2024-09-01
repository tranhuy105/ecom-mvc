package com.tranhuy105.admin.dto.ghn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GhnOrderResponse {
    private Integer code;
    private String message;
    private Data data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private String order_code;
        private LocalDateTime expected_delivery_time;
        private String total_fee;
        private String sort_code;
    }
}