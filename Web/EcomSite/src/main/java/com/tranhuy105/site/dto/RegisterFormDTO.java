package com.tranhuy105.site.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterFormDTO {
    @NotNull @NotEmpty
    private String firstName;
    @NotNull @NotEmpty
    private String lastName;
    @Email @NotEmpty @NotNull
    private String email;
    @NotNull @NotEmpty @Size(min = 3)
    private String password;

    @Size(max = 15)
    private String phoneNumber;

    // main address
    @NotNull @NotEmpty
    private String addressLine1;
    @NotNull @NotEmpty
    private String addressLine2;
    @NotNull @NotEmpty
    private String city;
    @NotNull @NotEmpty
    private Integer country;
    @NotNull @NotEmpty
    private String state;
    @NotNull @NotEmpty
    private String postalCode;
}
