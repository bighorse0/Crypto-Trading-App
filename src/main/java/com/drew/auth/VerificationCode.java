package com.drew.auth;

import com.drew.entity.User;
import com.drew.entity.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String OTP;

    @OneToOne
    private User user;

    private String email;

    private String phoneNumber;

    private VerificationType verificationType;
}
