package com.drew.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String OTP;
    private String password;
}
