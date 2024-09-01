package com.tranhuy105.admin.repository;

import com.tranhuy105.admin.dto.OrderItemDTO;
import com.tranhuy105.common.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query(nativeQuery = true, value =
            "SELECT " +
                    "ot.id AS id, " +
                    "sk.sku_code AS skuCode, " +
                    "p.name AS productName, " +
                    "p.alias AS productAlias, " +
                    "pi.name AS productImage, " +
                    "ot.quantity AS quantity, " +
                    "ot.price AS originalPrice, " +
                    "ot.discount_percent AS discountedPercent, " +
                    "ot.total_amount AS finalAmount " +
                    "FROM order_items ot " +
                    "LEFT JOIN skus sk ON ot.sku_id = sk.id " +
                    "LEFT JOIN products p ON sk.product_id = p.id " +
                    "LEFT JOIN product_images pi ON pi.product_id = p.id AND pi.is_main = 1 " +
                    "WHERE ot.order_id = :id")
    List<OrderItemDTO> findFullByOrderId(Integer id);
}
