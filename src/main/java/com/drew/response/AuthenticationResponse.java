package com.drew.response;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwt;
    private boolean status;
    private String message;
    private boolean twoFactorAuthEnabled;
    private String session;

}
