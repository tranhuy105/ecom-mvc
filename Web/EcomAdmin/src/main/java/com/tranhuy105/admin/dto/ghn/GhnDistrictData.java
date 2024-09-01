package com.tranhuy105.admin.dto.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GhnDistrictData {
    @JsonProperty("DistrictID")
    private Integer districtId;

    @JsonProperty("ProvinceID")
    private Integer provinceId;

    @JsonProperty("DistrictName")
    private String districtName;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Type")
    private Integer type;

    @JsonProperty("SupportType")
    private Integer supportType;

    @JsonProperty("CanUpdateCOD")
    private Boolean canUpdateCod;

    @JsonProperty("Status")
    private Integer status;
}

