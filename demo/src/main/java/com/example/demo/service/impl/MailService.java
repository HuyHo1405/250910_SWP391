package com.example.demo.service.impl;

import com.example.demo.exception.AuthException;
import com.example.demo.service.interfaces.IMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailService implements IMailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final String FROM_EMAIL = "noreply@example.com";

    @Async
    @Override
    public void sendVerificationMail(String toEmail, String verificationCode) {
        String subject = "Verify Your Email Address";
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);
        String htmlContent = templateEngine.process("verification-email", context);

        sendMail(toEmail, subject, htmlContent);
    }

    @Async
    @Override
    public void sendPasswordResetMail(String toEmail, String resetLink) {
        String subject = "Password Reset Request";
        String htmlContent = String.format(
                "Hello,<br><br>" +
                        "You have requested to reset your password. Click the link below to set a new password:<br>" +
                        "<a href=\"%s\">Reset Password</a><br><br>" +
                        "This link will expire in 24 hours.<br><br>" +
                        "If you didn't request this, please ignore this email.",
                resetLink
        );

        sendMail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendMail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = isHtml

            mailSender.send(message);
            log.info("Email sent to: {} | subject: {}", toEmail, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {} | subject: {}", toEmail, subject, e);
            throw new AuthException.EmailSendFailed();
        }
    }
}
