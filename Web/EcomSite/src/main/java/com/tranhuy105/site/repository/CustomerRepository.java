package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.AuthenticationType;
import com.tranhuy105.common.entity.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(@NonNull String email);
    Optional<Customer> findByVerificationCode(@NonNull String verificationCode);
    Optional<Customer> findByForgotPasswordCode(@NonNull String forgotPasswordCode);

    @EntityGraph(attributePaths = {"shoppingCart.cartItems"})
    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    Optional<Customer> findByIdWithShoppingCart(Integer id);

    @EntityGraph(attributePaths = {"shoppingCart.cartItems.sku"})
    @Query("SELECT c FROM Customer c " +
            "LEFT JOIN FETCH c.shoppingCart sc " +
            "LEFT JOIN FETCH sc.cartItems ci " +
            "LEFT JOIN FETCH ci.sku s " +
            "LEFT JOIN FETCH s.product " +
            "WHERE c.id = :id")
    Optional<Customer> findByIdWithShoppingCartItems(Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.enabled = TRUE, c.verificationCode = NULL WHERE c.id = :id")
    void enable(Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.authenticationType = :authenticationType WHERE c.id = :id")
    void updateAuthenticationType(Integer id, AuthenticationType authenticationType);
}
