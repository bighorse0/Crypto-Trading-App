package com.drew.service;

import com.drew.auth.TwoFactorAuthentication;
import com.drew.config.JwtProvider;
import com.drew.entity.User;
import com.drew.entity.VerificationType;
import com.drew.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public User findUserByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User not found");
        }

        return user;
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User not found");
        }

        return user;
    }


    @Override
    public User findUserById(Long id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new Exception("User not found");

        }

        return user.get();
    }

    @Override
    public User enableTwoFactorAuthentication(User user, VerificationType verificationType, String sendTo) {
        TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication();
        twoFactorAuthentication.setIsEnabled(true);
        twoFactorAuthentication.setSendTo(verificationType);

        user.setTwoFactorAuthentication(twoFactorAuthentication);
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
