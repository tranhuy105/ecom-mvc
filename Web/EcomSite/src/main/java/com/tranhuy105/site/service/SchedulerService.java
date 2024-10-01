package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.payment.OrderExpirationJob;
import com.tranhuy105.site.payment.PaymentExpiracyJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {
    private final Scheduler scheduler;

    public void schedulePaymentExpiryJob(Integer orderId, int delayInMinutes) {
        JobKey jobKey = JobKey.jobKey("payment-expiry-" + orderId, "payment");

        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.debug("Cancelled existing payment expiry job for order {}", orderId);
            }

            JobDetail jobDetail = JobBuilder.newJob(PaymentExpiracyJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("orderId", orderId)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("payment-expiry-trigger-" + orderId, "payment")
                    .startAt(DateBuilder.futureDate(delayInMinutes, DateBuilder.IntervalUnit.MINUTE))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            log.debug("Schedule payment expiration after {} minutes", delayInMinutes);
        } catch (SchedulerException e) {
            log.error("Failed to schedule payment expiry job for order {}", orderId, e);
        }
    }

    public void cancelPaymentExpiryJob(Integer orderId) {
        try {
            JobKey jobKey = JobKey.jobKey("payment-expiry-" + orderId, "payment");
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.debug("Successfully cancelled the payment expiry job for order {}", orderId);
            } else {
                log.warn("No payment expiry job found for order {}", orderId);
            }
        } catch (SchedulerException e) {
            log.error("Error while cancelling payment expiry job for order {}", orderId, e);
        }
    }

    public void scheduleOrderExpiration(Order order) {
        try {
            JobKey jobKey = JobKey.jobKey("order-expiration-" + order.getOrderNumber());
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.debug("Successfully cancel existing order expiration job for order " + order.getOrderNumber());
            }

            JobDetail jobDetail = JobBuilder.newJob(OrderExpirationJob.class)
                    .withIdentity("order-expiration-" + order.getOrderNumber())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .startAt(DateBuilder.futureDate(24, DateBuilder.IntervalUnit.HOUR))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Error scheduling order expiration job for order " + order.getOrderNumber(), e);
        }
    }

    public void cancelOrderExpirationJob(String orderNumber) {
        try {
            JobKey jobKey = JobKey.jobKey("order-expiration-" + orderNumber);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.debug("Successfully cancelled the order expiration job for order " + orderNumber);
            }
        } catch (SchedulerException e) {
            log.error("Error while cancelling order expiration job for order " + orderNumber, e);
        }
    }

}

