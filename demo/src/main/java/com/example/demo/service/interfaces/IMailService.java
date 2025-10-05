package com.example.demo.service.interfaces;

public interface IMailService {
    public void sendMail(String toEmail, String subject, String htmlContent);
    public void sendPasswordResetMail(String toEmail, String resetLink);
    public void sendVerificationMail(String toEmail, String verificationCode);
}
