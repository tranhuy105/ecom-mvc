package com.tranhuy105.admin.dto.ghn;

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
    private List<String> orderCodes;
}
