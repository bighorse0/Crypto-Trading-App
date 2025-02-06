package com.drew.service;

import com.drew.auth.TwoFactorOTP;
import com.drew.entity.User;

public interface TwoFactorOTPService {
        TwoFactorOTP createTwoFactorOTP(User user, String otp, String jwt);
        TwoFactorOTP findByUserId(Long userId);
        TwoFactorOTP findById(String id);
        boolean verifyTwoFactorOTP(TwoFactorOTP twoFactorotp, String otp);
        void deleteTwoFactorOTP(TwoFactorOTP twoFactorotp);
}
