package com.example.demo.service.interfaces;

import com.example.demo.model.entity.User;

public interface IVerificationCodeService {
    public String addVerificationCode(User user);
    public void verifyCode(User user, String code);
}
