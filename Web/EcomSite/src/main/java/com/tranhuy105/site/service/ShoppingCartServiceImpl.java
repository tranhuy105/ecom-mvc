package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.*;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.CartItemRepository;
import com.tranhuy105.site.repository.CustomerRepository;
import com.tranhuy105.site.repository.ShoppingCartRepository;
import com.tranhuy105.site.repository.SkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService{
    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerRepository customerRepository;
    private final SkuRepository skuRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public BigDecimal calculateSubtotalPrice(ShoppingCart shoppingCart) {
        if (shoppingCart == null || shoppingCart.getCartItems() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal subTotal = BigDecimal.ZERO;

        for (CartItem cartItem : shoppingCart.getCartItems()) {
            BigDecimal itemPrice = cartItem.getSku().getPrice();
            int quantity = cartItem.getQuantity();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            subTotal = subTotal.add(itemTotal);
        }

        return subTotal;
    }

    @Override
    public BigDecimal calculateTotalPrice(ShoppingCart shoppingCart) {
        if (shoppingCart == null || shoppingCart.getCartItems() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : shoppingCart.getCartItems()) {
            BigDecimal itemPrice = cartItem.getSku().getDiscountedPrice();
            int quantity = cartItem.getQuantity();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            totalPrice = totalPrice.add(itemTotal);
        }

        return totalPrice;
    }

    @Transactional
    @Override
    public void addItemToCart(Integer customerId, Integer skuId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid Quantity");
        }

        Customer customer = customerRepository.findByIdWithShoppingCart(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Sku sku = skuRepository.findById(skuId).orElseThrow(
                () -> new NotFoundException("Sku Not Found"));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            cart = new ShoppingCart();
            cart.setCustomer(customer);
            customer.setShoppingCart(cart);
        }

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getSku().getId().equals(skuId))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setSku(sku);
            cartItem.setShoppingCart(cart);
            cartItem.setQuantity(quantity);
            cart.getCartItems().add(cartItem);
        }

        shoppingCartRepository.save(cart);
    }


    @Transactional
    @Override
    public void updateCartItemQuantity(Integer customerId, Integer skuId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        Customer customer = customerRepository.findByIdWithShoppingCart(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            throw new NotFoundException("Shopping cart not found");
        }

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getSku().getId().equals(skuId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product not found in cart"));

        if (quantity == 0) {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
        }

        shoppingCartRepository.save(cart);
    }

    @Transactional
    @Override
    public void removeCartItem(Integer customerId, Integer skuId) {
        updateCartItemQuantity(customerId, skuId, 0);
    }

    @Transactional
    @Override
    public ShoppingCart getOrCreateCartForCustomer(Integer customerId) {
        Customer customer = customerRepository.findByIdWithShoppingCartItems(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            cart = new ShoppingCart();
            cart.setCustomer(customer);
            customer.setShoppingCart(cart);
            return shoppingCartRepository.save(cart);
        }
        return cart;
    }

    @Override
    public void clearShoppingCart(Integer customerId) {
        Customer customer = customerRepository.findByIdWithShoppingCart(customerId).orElse(null);

        if (customer != null) {
            ShoppingCart cart = customer.getShoppingCart();
            cartItemRepository.deleteByShoppingCartId(cart.getId());
        }
    }
}
