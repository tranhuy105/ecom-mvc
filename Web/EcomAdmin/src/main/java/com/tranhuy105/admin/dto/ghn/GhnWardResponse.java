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
public class GhnWardResponse {
    private Integer code;
    private String message;
    private List<GhnWardData> data;
}
