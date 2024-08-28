package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.service.CustomerService;
import com.tranhuy105.site.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final ShoppingCartService cartService;
    private final CustomerService customerService;

    @PostMapping("/cart")
    public ResponseEntity<String> addItemToCart(
            @RequestParam("sku") Integer skuId,
            @RequestParam("quantity") Integer quantity,
            Authentication authentication
    ) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            cartService.addItemToCart(customer.getId(), skuId, quantity);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/cart")
    public ResponseEntity<String> updateCartItemQuantity(
            @RequestParam("sku") Integer skuId,
            @RequestParam("quantity") Integer quantity,
            Authentication authentication
    ) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            cartService.updateCartItemQuantity(customer.getId(), skuId, quantity);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

}
