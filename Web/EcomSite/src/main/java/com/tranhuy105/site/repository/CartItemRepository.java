package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.CartItem;
import org.springframework.data.repository.CrudRepository;

public interface CartItemRepository extends CrudRepository<CartItem, Integer> {
}
