package com.drew.repository;

import com.drew.entity.ForgotPasswordToken;
import com.drew.service.ForgotPasswordService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, String> {

    ForgotPasswordToken findByUserId(Long userId);
}
