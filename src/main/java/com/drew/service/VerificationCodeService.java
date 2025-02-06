package com.drew.service;

import com.drew.auth.VerificationCode;
import com.drew.entity.User;
import com.drew.entity.VerificationType;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;

    VerificationCode getVerificationCodeByUserId(Long userId);

    void deleteVerificationCodeByUserId(VerificationCode verificationCode);
}
