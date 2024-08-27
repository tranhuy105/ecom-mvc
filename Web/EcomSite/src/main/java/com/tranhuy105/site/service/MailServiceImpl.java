package com.tranhuy105.site.service;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();
    private static final int CODE_LENGTH = 32;
    private final SettingService settingService;

    @Override
    public String generateVerificationCode() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String code = base64Encoder.encodeToString(randomBytes);

        if (code.length() > CODE_LENGTH) {
            code = code.substring(0, CODE_LENGTH);
        }

        return code;
    }

    @Override
    public JavaMailSender prepareMailSender() {
        String host = settingService.getSettingByKey("MAIL_HOST");
        String portStr = settingService.getSettingByKey("MAIL_PORT");
        String username = settingService.getSettingByKey("MAIL_USERNAME");
        String password = settingService.getSettingByKey("MAIL_PASSWORD");
        String auth = settingService.getSettingByKey("SMTP_AUTH");
        String secured = settingService.getSettingByKey("SMTP_SECURED");

        validateMailSettings(host, portStr, username, password, auth, secured);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(portStr));
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", secured);
        props.put("mail.debug", "false");

        return mailSender;
    }

    @Override
    public void sendVerificationEmail(Customer customer) throws MessagingException, UnsupportedEncodingException {
        String from = settingService.getSettingByKey("MAIL_FROM");
        String senderName = settingService.getSettingByKey("MAIL_SENDER_NAME");
        String subject = settingService.getSettingByKey("CUSTOMER_VERIFY_SUBJECT");
        String content = settingService.getSettingByKey("CUSTOMER_VERIFY_CONTENT");

        validateEmailContentSettings(from, senderName, subject, content);
        String verificationUrl = getCurrentSiteDomain()+ "/verify?code=" + customer.getVerificationCode();

        content = content.replace("[[VERIFY_URL]]", verificationUrl);
        content = content.replace("[[FULLNAME]]", customer.getFullName());

        prepareAndSendMimeMessage(from, customer.getEmail(), senderName, subject, content);
    }

    @Override
    public void sendResetPasswordMail(Customer customer) throws MessagingException, UnsupportedEncodingException {
        String from = settingService.getSettingByKey("MAIL_FROM");
        String senderName = settingService.getSettingByKey("MAIL_SENDER_NAME");
        String subject = "Here's the link to reset your password!";
        String verificationUrl = getCurrentSiteDomain()+ "/account/reset?code=" + customer.getForgotPasswordCode();
        String content = "<p>Hello " + customer.getFullName() + ",</p>" +
                        "<p>You have requested to reset your password. Please click the link below to reset it:</p>" +
                        "<p><a href=\"" + verificationUrl + "\">Reset your password</a></p>" +
                        "<p>If you did not request this, please ignore this email and your password will remain unchanged.</p>" +
                        "<p>Best regards,<br>" + senderName + "</p>";

        validateEmailContentSettings(from, senderName, subject, content);


        prepareAndSendMimeMessage(from, customer.getEmail(), senderName, subject, content);
    }

    @Override
    public void prepareAndSendMimeMessage(String from,
                                           String to,
                                           String senderName,
                                           String subject,
                                           String content) throws MessagingException, UnsupportedEncodingException {
        JavaMailSender mailSender = prepareMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        helper.setFrom(from, senderName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(mimeMessage);
    }

    private String getCurrentSiteDomain() {
        String siteDomain =  settingService.getSettingByKey("SITE_URL");
        if (siteDomain == null) {
            throw new IllegalArgumentException("Site Domain Is Not Configured For Mail");
        }

        return siteDomain;
    }

    private void validateEmailContentSettings(String from, String senderName, String subject, String content) {
        if (from == null || from.isEmpty()) {
            throw new IllegalStateException("MAIL_FROM setting is missing or empty");
        }
        if (senderName == null || senderName.isEmpty()) {
            throw new IllegalStateException("MAIL_SENDER_NAME setting is missing or empty");
        }
        if (subject == null || subject.isEmpty()) {
            throw new IllegalStateException("CUSTOMER_VERIFY_SUBJECT setting is missing or empty");
        }
        if (content == null || content.isEmpty()) {
            throw new IllegalStateException("CUSTOMER_VERIFY_CONTENT setting is missing or empty");
        }
    }

    private void validateMailSettings(String host, String portStr, String username, String password, String auth, String secured) {
        if (host == null || host.isEmpty()) {
            throw new IllegalStateException("MAIL_HOST setting is missing or empty");
        }
        if (portStr == null || portStr.isEmpty()) {
            throw new IllegalStateException("MAIL_PORT setting is missing or empty");
        }
        try {
            Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("MAIL_PORT setting is not a valid integer", e);
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalStateException("MAIL_USERNAME setting is missing or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("MAIL_PASSWORD setting is missing or empty");
        }
        if (auth == null || auth.isEmpty()) {
            throw new IllegalStateException("SMTP_AUTH setting is missing or empty");
        }
        if (secured == null || secured.isEmpty()) {
            throw new IllegalStateException("SMTP_SECURED setting is missing or empty");
        }
    }
}
