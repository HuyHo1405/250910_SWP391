package com.example.demo.service.impl;

import com.example.demo.exception.UserException.*;
import com.example.demo.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final UserRepo userRepo;

    public void checkEmailAndPhoneAvailability(String email, String phone) {
        checkEmailAvailability(email);
        checkPhoneAvailability(phone);
    }

    public void checkEmailAvailability(String email) {
        if (userRepo.findByEmailAddress(email).isPresent()) {
            throw new EmailAlreadyExists();
        }
    }

    public void checkPhoneAvailability(String phone) {
        if (userRepo.findByPhoneNumber(phone).isPresent()) {
            throw new PhoneAlreadyExists();
        }
    }
}