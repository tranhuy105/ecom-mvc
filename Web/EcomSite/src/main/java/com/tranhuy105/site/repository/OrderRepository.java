package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.dto.OrderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @NonNull
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.payment LEFT JOIN FETCH o.statusHistory WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithPayment(@NonNull String orderNumber);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems ot " +
            "LEFT JOIN FETCH ot.sku " +
            "WHERE o.reservationExpiry < :now AND o.status = 'PENDING'")
    List<Order> findExpiredOrders(LocalDateTime now);

    @Query("SELECT o.orderNumber AS orderNumber, " +
            "o.createdAt AS createdAt, " +
            "o.updatedAt AS updatedAt, " +
            "a.addressLine1 AS addressLine1, " +
            "a.addressLine2 AS addressLine2, " +
            "a.city AS city, " +
            "a.state AS state, " +
            "a.postalCode AS postalCode, " +
            "o.finalAmount AS amount, " +
            "o.status AS orderStatus " +
            "FROM Order o LEFT JOIN o.shippingAddress a " +
            "WHERE o.customer.id = :customerId")
    List<OrderDTO> findOrderDTOsByCustomerId(Integer customerId);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "LEFT JOIN FETCH o.payment " +
            "LEFT JOIN FETCH o.statusHistory " +
            "WHERE o.orderNumber = :orderNumber")
    Optional<Order> findOrderDetail(String orderNumber);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems ot " +
            "LEFT JOIN FETCH ot.sku " +
            "WHERE o = :order")
    Order lazyFetchItem(Order order);
}
