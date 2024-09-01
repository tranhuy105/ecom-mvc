package com.tranhuy105.admin.repository;

import com.tranhuy105.admin.dto.OrderOverviewDTO;
import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.constant.ShippingStatus;
import com.tranhuy105.common.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o.orderNumber as orderNumber, " +
            "o.customer.id as customerId, " +
            "o.totalAmount as totalAmount, " +
            "o.finalAmount as finalAmount, " +
            "o.status as status, " +
            "o.shippingStatus as shippingStatus, " +
            "o.paymentStatus as paymentStatus " +
            "FROM Order o WHERE " +
            "(:orderStatus IS NULL OR o.status = :orderStatus) AND " +
            "(:shippingStatus IS NULL OR o.shippingStatus = :shippingStatus) AND " +
            "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus) AND " +
            "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR o.createdAt <= :endDate) AND " +
            "(:search IS NULL OR o.orderNumber LIKE %:search%)")
    Page<OrderOverviewDTO> searchOrders(@Param("orderStatus") OrderStatus orderStatus,
                                        @Param("shippingStatus") ShippingStatus shippingStatus,
                                        @Param("paymentStatus") PaymentStatus paymentStatus,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        @Param("search") String search,
                                        Pageable pageable);

    @EntityGraph(attributePaths = {"shippingAddress", "statusHistory", "payment"})
    Optional<Order> findByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = {"shippingAddress", "statusHistory", "payment"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByOrderId(Integer id);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "LEFT JOIN FETCH o.customer " +
            "LEFT JOIN FETCH o.payment " +
            "LEFT JOIN FETCH o.statusHistory " +
            "WHERE o.orderNumber = :orderNumber ")
    Order findOrderDetailByOrderNumber(String orderNumber);
}
