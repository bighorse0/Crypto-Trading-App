package com.drew.service;

import com.drew.entity.ForgotPasswordToken;
import com.drew.entity.User;
import com.drew.entity.VerificationType;

public interface ForgotPasswordService {

    ForgotPasswordToken createForgotPasswordToken(User user, String OTP, VerificationType verificationType, String sendTo, String id);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUserId(Long userId);

    void deleteToken(ForgotPasswordToken token);
}
