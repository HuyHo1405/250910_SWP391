package com.example.demo.service.interfaces;

import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Vehicle;

import java.time.LocalDateTime;

public interface IMailService {
    public void sendMail(String toEmail, String subject, String htmlContent);
    public void sendGeneratedPassword(String toEmail, String password);
    public void sendPasswordResetMail(String toEmail, String resetLink);
    public void sendVerificationMail(String toEmail, String verificationCode);
    public void sendReminderMail(User user, Vehicle vehicle, double threshold);
}
