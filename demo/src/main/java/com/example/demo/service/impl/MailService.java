package com.example.demo.service.impl;

import com.example.demo.exception.AuthException;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Vehicle;
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

import java.time.LocalDateTime;


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
        String htmlContent = String.format(
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                        "Hello,<br><br>" +
                        "You have requested to verify your account. Here is the verification code:<br><br>" +
                        "<div style='background-color: #f4f4f4; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0;'>" +
                        "<span style='font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #2c3e50;'>%s</span>" +
                        "</div>" +
                        "This code will expire in 24 hours.<br><br>" +
                        "If you didn't request this, please ignore this email." +
                        "</div>",
                verificationCode
        );

        sendMail(toEmail, subject, htmlContent);
    }

    @Override
    @Async
    public void sendReminderMail(User user, Vehicle vehicle, double threshold) {
        String toEmail = user.getEmailAddress();
        String subject = "Vehicle Maintenance Reminder";

        String htmlContent = String.format(
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                        "Hello %s,<br><br>" +
                        "Your vehicle (plate number <b>%s</b>, VIN <b>%s</b>) is approaching the next maintenance milestone:<br><br>" +
                        "<div style='background-color: #fafafa; padding: 15px; border-radius: 8px;'>" +
                        "<strong>Upcoming milestone:</strong> <span style='color:#e67e22; font-size:18px;'>%.0f km</span><br>" +
                        "<strong>Recommended action:</strong> Please schedule a service appointment as soon as possible.<br>" +
                        "</div><br>" +
                        "Please access the application or contact the service center to book your appointment.<br><br>" +
                        "Thank you for choosing our service.<br>" +
                        "</div>",
                user.getFullName(),
                vehicle.getPlateNumber(),
                vehicle.getVin(),
                threshold
        );

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

    @Async
    @Override
    public void sendGeneratedPassword(String toEmail, String password) {
        String subject = "Password for Your Account";
        Context context = new Context();
        String htmlContent = String.format(
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                        "Hello,<br><br>" +
                        "This is the password for your account to access in the web application:<br><br>" +
                        "<div style='background-color: #f4f4f4; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0;'>" +
                        "<span style='font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #2c3e50;'>%s</span>" +
                        "</div>" +
                        "If you didn't request this, please ignore this email." +
                        "</div>",
                password
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
