package com.tranhuy105.site.dto;

import com.tranhuy105.common.entity.AuthenticationType;
import com.tranhuy105.common.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String password = "";
    private String phoneNumber;
    private String profilePictureUrl;
    private LocalDateTime dateOfBirth;
    private LocalDateTime updatedAt;
    private AuthenticationType authenticationType;

    public AccountDTO(Customer customer) {
        if (customer == null) {
            return;
        }
        this.id = customer.getId();
        this.email = customer.getEmail();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.phoneNumber = customer.getPhoneNumber();
        this.profilePictureUrl = customer.getProfilePictureUrl();
        this.dateOfBirth = customer.getDateOfBirth();
        this.updatedAt = customer.getUpdatedAt();
        this.authenticationType = customer.getAuthenticationType();
    }
}
