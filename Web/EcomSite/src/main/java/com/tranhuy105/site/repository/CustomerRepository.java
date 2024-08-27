package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Customer;
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

    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.enabled = TRUE, c.verificationCode = NULL WHERE c.id = :id")
    void enable(Integer id);
}