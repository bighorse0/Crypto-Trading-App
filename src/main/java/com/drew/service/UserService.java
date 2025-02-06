package com.drew.service;

import com.drew.entity.User;
import com.drew.entity.VerificationType;

public interface UserService {
    User findUserByJwt(String jwt) throws Exception;
    User findUserByEmail(String email) throws Exception;
    User findUserById(Long id) throws Exception;
    User enableTwoFactorAuthentication(User user, VerificationType verificationType, String sendTo) throws Exception;
    User updatePassword(User user, String newPassword);

}

