package com.drew.auth;

import com.drew.entity.VerificationType;
import lombok.Data;

@Data
public class TwoFactorAuthentication
{
    private Boolean isEnabled = false;
    private VerificationType sendTo;

}
