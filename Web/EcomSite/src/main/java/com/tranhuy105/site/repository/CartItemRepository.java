package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.CartItem;
import com.tranhuy105.common.entity.ShoppingCart;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CartItemRepository extends CrudRepository<CartItem, Integer> {
    @Query("DELETE FROM CartItem c WHERE c.shoppingCart.id = :id")
    @Modifying
    @Transactional
    void deleteByShoppingCartId(Integer id);
}
