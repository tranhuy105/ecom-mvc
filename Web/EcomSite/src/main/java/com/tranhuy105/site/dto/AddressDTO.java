package com.tranhuy105.site.dto;

import com.tranhuy105.common.entity.Address;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AddressDTO {
    private Integer id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private boolean mainAddress;
    private Integer countryId;

    public AddressDTO(Address address) {
        this.id = address.getId();
        this.addressLine2 = address.getAddressLine2();
        this.addressLine1 = address.getAddressLine1();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.mainAddress = address.isMainAddress();
        this.countryId = address.getCountry().getId();
    }
}
