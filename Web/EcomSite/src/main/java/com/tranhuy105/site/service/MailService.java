package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.common.entity.Order;
import jakarta.mail.MessagingException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;

public interface MailService {
    String generateVerificationCode();
    JavaMailSender prepareMailSender();
    void sendVerificationEmail(Customer customer) throws MessagingException, UnsupportedEncodingException;
    void sendResetPasswordMail(Customer customer) throws MessagingException, UnsupportedEncodingException;
    void prepareAndSendMimeMessage(String from, String to, String senderName, String subject, String content) throws MessagingException, UnsupportedEncodingException;
}
