package com.drew.service;

import com.drew.auth.TwoFactorOTP;
import com.drew.entity.User;
import com.drew.repository.TwoFactorOTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TwoFactorOTPServiceImpl implements TwoFactorOTPService {

    @Autowired
    private TwoFactorOTPRepository twoFactorOTPRepository;


    @Override
    public TwoFactorOTP createTwoFactorOTP(User user, String otp, String jwt) {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setOTP(otp);
        twoFactorOTP.setJwtToken(jwt);
        twoFactorOTP.setId(id);
        twoFactorOTP.setUser(user);

        return twoFactorOTPRepository.save(twoFactorOTP);
    }

    @Override
    public TwoFactorOTP findByUserId(Long userId) {
        return twoFactorOTPRepository.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        Optional<TwoFactorOTP> twoFactorOTP = twoFactorOTPRepository.findById(id);
        return twoFactorOTP.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOTP(TwoFactorOTP twoFactorotp, String otp) {
        return twoFactorotp.getOTP().equals(otp);
    }

    @Override
    public void deleteTwoFactorOTP(TwoFactorOTP twoFactorotp) {
        twoFactorOTPRepository.delete(twoFactorotp);
    }
}
