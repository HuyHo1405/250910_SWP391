package com.example.demo.service.interfaces;

public interface IMailService {
    public void sendVerificationEmail(String email, String code);
}
