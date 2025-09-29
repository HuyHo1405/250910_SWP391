package com.example.demo.service.interfaces;

import com.example.demo.exception.AuthException.EmailSendFailed;
import com.example.demo.service.impl.IMailService;
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
public class EmailService implements IMailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final String VERIFICATION_EMAIL_TEMPLATE = "verification-email";
    private static final String FROM_EMAIL = "noreply@example.com";
    private static final String VERIFICATION_EMAIL_SUBJECT = "Verify Your Email Address";

    @Async
    @Override
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties
            helper.setFrom(FROM_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject(VERIFICATION_EMAIL_SUBJECT);

            // Prepare the evaluation context
            Context context = new Context();
            context.setVariable("verificationCode", verificationCode);

            // Process the template
            String htmlContent = templateEngine.process(VERIFICATION_EMAIL_TEMPLATE, context);
            helper.setText(htmlContent, true); // true = isHtml

            // Send the email
            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new EmailSendFailed();
        }
    }
}