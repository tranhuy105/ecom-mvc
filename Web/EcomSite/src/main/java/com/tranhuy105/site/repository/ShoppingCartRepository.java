package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> { }
