package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByOrderId(Integer orderId);
}
