package com.tranhuy105.site.payment;

import com.tranhuy105.site.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExpirationJob implements Job {

    private final OrderService orderService;

    @Override
    public void execute(JobExecutionContext context) {
        String orderNumber = context.getJobDetail().getKey().getName().replace("order-expiration-", "");
        try {
            orderService.expireOrder(orderNumber);
        } catch (OrderNotFoundException e) {
            log.warn(e.getMessage());
        }
    }
}
