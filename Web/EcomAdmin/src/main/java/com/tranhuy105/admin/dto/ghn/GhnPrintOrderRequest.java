package com.tranhuy105.admin.dto.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GhnPrintOrderRequest {
    @JsonProperty("order_codes")
    private List<String> orderCodes;
}
