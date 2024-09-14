package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.setting.SettingService;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class MailServiceTest {

    @Mock
    private SettingService settingService;

    @InjectMocks
    private MailServiceImpl mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateVerificationCode() {
        String code = mailService.generateVerificationCode();
        assertNotNull(code);
        assertEquals(32, code.length());
    }

    @Test
    void testPrepareMailSender() {
        when(settingService.getSettingByKey("MAIL_HOST")).thenReturn("smtp.example.com");
        when(settingService.getSettingByKey("MAIL_PORT")).thenReturn("587");
        when(settingService.getSettingByKey("MAIL_USERNAME")).thenReturn("username");
        when(settingService.getSettingByKey("MAIL_PASSWORD")).thenReturn("password");
        when(settingService.getSettingByKey("SMTP_AUTH")).thenReturn("true");
        when(settingService.getSettingByKey("SMTP_SECURED")).thenReturn("true");

        JavaMailSender mailSender = mailService.prepareMailSender();

        assertNotNull(mailSender);
        assertTrue(mailSender instanceof JavaMailSenderImpl);

        JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;
        assertEquals("smtp.example.com", mailSenderImpl.getHost());
        assertEquals(587, mailSenderImpl.getPort());
    }

    @Test
    void testSendVerificationEmail() throws MessagingException, UnsupportedEncodingException {
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setVerificationCode("1234567890abcdef");

        when(settingService.getSettingByKey("MAIL_FROM")).thenReturn("no-reply@example.com");
        when(settingService.getSettingByKey("MAIL_SENDER_NAME")).thenReturn("Example");
        when(settingService.getSettingByKey("CUSTOMER_VERIFY_SUBJECT")).thenReturn("Verify Your Email");
        when(settingService.getSettingByKey("CUSTOMER_VERIFY_CONTENT")).thenReturn("Click here to verify: [[VERIFY_URL]].");
        when(settingService.getSettingByKey("SITE_URL")).thenReturn("http://localhost:80/site");

        JavaMailSender mockMailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mockMailSender).send(any(MimeMessage.class));

        when(mimeMessage.getFrom()).thenReturn(new Address[] { new InternetAddress("no-reply@example.com", "Example") });
        when(mimeMessage.getAllRecipients()).thenReturn(new Address[] { new InternetAddress("test@example.com") });
        when(mimeMessage.getSubject()).thenReturn("Verify Your Email");

        mailService = new MailServiceImpl(settingService) {
            @Override
            public JavaMailSender prepareMailSender() {
                return mockMailSender;
            }
        };

        mailService.sendVerificationEmail(customer);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mockMailSender).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);

        assertEquals("no-reply@example.com", ((InternetAddress) sentMessage.getFrom()[0]).getAddress());
        assertEquals("test@example.com", ((InternetAddress) sentMessage.getAllRecipients()[0]).getAddress());
        assertEquals("Verify Your Email", sentMessage.getSubject());
    }

    @Test
    void testSendResetPasswordMail() throws MessagingException, IOException {
        String resetCode = "reset-code";
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setForgotPasswordCode(resetCode);

        String content = "<p>Hello " + customer.getFullName() + ",</p>" +
                "<p>You have requested to reset your password. Please click the link below to reset it:</p>" +
                "<p><a href=\"" + resetCode + "\">Reset your password</a></p>" +
                "<p>If you did not request this, please ignore this email and your password will remain unchanged.</p>" +
                "<p>Best regards,<br>" + customer.getFullName() + "</p>";

        when(settingService.getSettingByKey("SITE_URL")).thenReturn("http://localhost:80/site");
        when(settingService.getSettingByKey("MAIL_FROM")).thenReturn("no-reply@example.com");
        when(settingService.getSettingByKey("MAIL_SENDER_NAME")).thenReturn("Example");

        JavaMailSender mockMailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mockMailSender).send(any(MimeMessage.class));

        when(mimeMessage.getFrom()).thenReturn(new Address[] { new InternetAddress("no-reply@example.com", "Example") });
        when(mimeMessage.getAllRecipients()).thenReturn(new Address[] { new InternetAddress("test@example.com") });
        when(mimeMessage.getSubject()).thenReturn("Here's the link to reset your password!");
        when(mimeMessage.getContent()).thenReturn(content);

        mailService = new MailServiceImpl(settingService) {
            @Override
            public JavaMailSender prepareMailSender() {
                return mockMailSender;
            }
        };

        mailService.sendResetPasswordMail(customer);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mockMailSender).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);

        assertEquals("no-reply@example.com", ((InternetAddress) sentMessage.getFrom()[0]).getAddress());
        assertEquals("test@example.com", ((InternetAddress) sentMessage.getAllRecipients()[0]).getAddress());
        assertTrue(sentMessage.getContent().toString().contains(resetCode));
    }

    @Test
    void testSendVerificationEmail_MissingMailFrom() {
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setVerificationCode("1234567890abcdef");

        when(settingService.getSettingByKey("MAIL_FROM")).thenReturn(null);
        when(settingService.getSettingByKey("MAIL_SENDER_NAME")).thenReturn("Example");
        when(settingService.getSettingByKey("CUSTOMER_VERIFY_SUBJECT")).thenReturn("Verify Your Email");
        when(settingService.getSettingByKey("CUSTOMER_VERIFY_CONTENT")).thenReturn("Click here to verify: [[VERIFY_URL]].");
        when(settingService.getSettingByKey("SITE_URL")).thenReturn("http://localhost:80/site");

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mailService.sendVerificationEmail(customer);
        });

        assertEquals("MAIL_FROM setting is missing or empty", exception.getMessage());
    }
}
