package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.entity.Payment;
import com.tranhuy105.site.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentExpiracyJob implements Job {
    private final PaymentRepository paymentRepository;

    @Override
    public void execute(JobExecutionContext context) {
        Integer orderId = context.getJobDetail().getJobDataMap().getInt("orderId");

        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null && payment.getStatus().equals(PaymentStatus.PENDING)) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
            log.info("Payment for order {} has expired.", orderId);
        }
    }
}
