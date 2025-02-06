package com.drew.service;

import com.drew.auth.VerificationCode;
import com.drew.entity.User;
import com.drew.entity.VerificationType;
import com.drew.repository.VerificationCodeRepository;
import com.drew.utils.OTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;


    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
        VerificationCode newVerificationCode = new VerificationCode();
        newVerificationCode.setOTP(OTPUtils.generateOTP());
        newVerificationCode.setVerificationType(verificationType);
        newVerificationCode.setUser(user);
        return verificationCodeRepository.save(newVerificationCode);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);

        if (verificationCode.isPresent()) {
            return verificationCode.get();
        }

        throw new Exception("VerificationCode not found");
    }

    @Override
    public VerificationCode getVerificationCodeByUserId(Long userId) {
        return verificationCodeRepository.findByUserId(userId);
    }

    @Override
    public void deleteVerificationCodeByUserId(VerificationCode verificationCode) {
        verificationCodeRepository.delete(verificationCode);
    }
}
