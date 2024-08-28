package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface ShoppingCartService {

    BigDecimal calculateTotalPrice(ShoppingCart shoppingCart);

    @Transactional
    void addItemToCart(Integer customerId, Integer skuId, int quantity);

    @Transactional
    void updateCartItemQuantity(Integer customerId, Integer skuId, int quantity);

    @Transactional
    void removeCartItem(Integer customerId, Integer skuId);

    @Transactional
    ShoppingCart getOrCreateCartForCustomer(Integer customerId);
}
