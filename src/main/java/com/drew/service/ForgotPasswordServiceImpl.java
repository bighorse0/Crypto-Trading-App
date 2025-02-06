package com.drew.service;

import com.drew.entity.ForgotPasswordToken;
import com.drew.entity.User;
import com.drew.entity.VerificationType;
import com.drew.repository.ForgotPasswordRepository;
import com.drew.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Override
    public ForgotPasswordToken createForgotPasswordToken(User user, String OTP, VerificationType verificationType, String sendTo, String id) {
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setUser(user);
        token.setOTP(OTP);
        token.setVerificationType(verificationType);
        token.setSendTo(sendTo);
        token.setId(id);
        return forgotPasswordRepository.save(token);
    }

    @Override
    public ForgotPasswordToken findById(String id) {
        Optional<ForgotPasswordToken> token = forgotPasswordRepository.findById(id);
        return token.orElse(null);
    }

    @Override
    public ForgotPasswordToken findByUserId(Long userId) {
        return forgotPasswordRepository.findByUserId(userId);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordRepository.delete(token);
    }
}
