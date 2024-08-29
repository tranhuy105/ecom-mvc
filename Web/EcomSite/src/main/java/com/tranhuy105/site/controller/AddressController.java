package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Address;
import com.tranhuy105.common.entity.Country;
import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.dto.AddressDTO;
import com.tranhuy105.site.service.AddressService;
import com.tranhuy105.site.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final CustomerService customerService;

    @GetMapping("/customer/address")
    public String customerAddressView(Authentication authentication, Model model) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            model.addAttribute("pageTitle", "Unauthorized");
            model.addAttribute("message", "please log in to see your address");
            return "message";
        }

        List<Address> addresses = addressService.findByCustomerId(customer.getId());
        model.addAttribute("addressList", addresses);

        return "address/address";
    }

    @GetMapping("/customer/address/new")
    public String createAddressView(Model model) {
        model.addAttribute("countriesList", customerService.findAvailableCountry());
        model.addAttribute("address", new AddressDTO());
        return "address/address-form";
    }

    @GetMapping("/customer/address/edit/{id}")
    public String editAddressView(Model model,
                                  @PathVariable Integer id,
                                  Authentication authentication) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            model.addAttribute("pageTitle", "Unauthorized");
            model.addAttribute("message", "please log in to see your address");
            return "message";
        }

        Address address = addressService.findById(id);
        if (address == null) {
            model.addAttribute("pageTitle", "Not Found");
            model.addAttribute("message", "Address Not Found");
            return "message";
        }

        if (!address.getCustomer().getId().equals(customer.getId())) {
            model.addAttribute("pageTitle", "Forbidden");
            model.addAttribute("message", "You dont have permission to see this content");
            return "message";
        }

        Integer countryId = address.getCountry().getId();

        List<Country> countries = customerService.findAvailableCountry();
        address.setCountry(
                countries.stream()
                .filter(country -> country.getId().equals(countryId))
                .findFirst()
                .orElse(null)
        );

        model.addAttribute("countriesList", countries);
        model.addAttribute("address", new AddressDTO(address));
        model.addAttribute("pageTitle", "Edit Address");
        return "address/address-form";
    }

    @GetMapping("/customer/address/{id}/default")
    public String setNewDefaultAddress(Model model,
                                       RedirectAttributes redirectAttributes,
                                       @PathVariable Integer id,
                                       Authentication authentication) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            model.addAttribute("pageTitle", "Unauthorized");
            model.addAttribute("message", "please log in to see your address");
            return "message";
        }

        try {
            addressService.updateDefaultAddress(id, customer);
            redirectAttributes.addFlashAttribute("message", "Success");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return "redirect:/customer/address";
    }

    @GetMapping("/customer/address/{id}/delete")
    public String deleteAddress(Model model,
                                       RedirectAttributes redirectAttributes,
                                       @PathVariable Integer id,
                                       Authentication authentication) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            model.addAttribute("pageTitle", "Unauthorized");
            model.addAttribute("message", "please log in to see your address");
            return "message";
        }

        try {
            addressService.deleteAddress(id, customer);
            redirectAttributes.addFlashAttribute("message", "Address Deleted");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return "redirect:/customer/address";
    }

    @PostMapping("/customer/address")
    public String saveAddress(AddressDTO address,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            model.addAttribute("pageTitle", "Unauthorized");
            model.addAttribute("message", "please log in to see your address");
            return "message";
        }

        try {
            if (address.getId() == null) {
                addressService.createNewAddress(DTOtoEntity(address), customer);
                System.out.println("create new adress");
            } else {
                System.out.println("update addres" + address.getId());
                addressService.updateAddress(DTOtoEntity(address), customer);
            }
            redirectAttributes.addFlashAttribute("message", "Address Saved");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/customer/address";
    }


    private Address DTOtoEntity(AddressDTO dto) {
        return new Address(dto.getId(),
                dto.getAddressLine1(),
                dto.getAddressLine2(),
                dto.getCity(),
                dto.getState(),
                dto.getPostalCode(),
                null,
                new Country(dto.getCountryId(), null, null),
                dto.isMainAddress());
    }
}
