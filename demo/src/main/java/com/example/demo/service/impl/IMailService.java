package com.example.demo.service.impl;

public interface IMailService {
    public void sendVerificationEmail(String email, String code);
}
