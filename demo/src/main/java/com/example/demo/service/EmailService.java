package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Email Verification");
        helper.setText(createVerificationEmailContent(token), true);

        mailSender.send(message);
    }

    private String createVerificationEmailContent(String token) {
        return String.format("""
                <html>
                <body>
                    <h2>Verify Your Email</h2>
                    <p>Please click the link below to verify your email address:</p>
                    <a href="http://localhost:8080/api/auth/verify?token=%s">Verify Email</a>
                    <p>This link will expire in 24 hours.</p>
                </body>
                </html>
                """, token);
    }
}
