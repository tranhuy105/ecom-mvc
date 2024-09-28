package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.common.entity.OrderItem;
import com.tranhuy105.common.entity.Sku;
import com.tranhuy105.site.event.OrderCancelledEvent;
import com.tranhuy105.site.event.OrderCreatedEvent;
import com.tranhuy105.site.exception.StockUnavailableException;
import com.tranhuy105.site.repository.OrderRepository;
import com.tranhuy105.site.repository.SkuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final SkuRepository skuRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void reserveStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Sku sku = orderItem.getSku();
            int newQuantity = sku.getStockQuantity() - orderItem.getQuantity();

            if (newQuantity < 0) {
                throw new StockUnavailableException("Not enough stock for SKU: " + sku.getSkuCode());
            }

            sku.setStockQuantity(newQuantity);
            skuRepository.save(sku);
        }
    }

    @Override
    @Transactional
    public void releaseStock(Order order) {
        order = orderRepository.lazyFetchItem(order);
        for (OrderItem orderItem : order.getOrderItems()) {
            Sku sku = orderItem.getSku();
            sku.setStockQuantity(sku.getStockQuantity() + orderItem.getQuantity());
            skuRepository.save(sku);
        }
    }
}