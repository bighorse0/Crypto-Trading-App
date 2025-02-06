package com.drew.controller;

import com.drew.auth.VerificationCode;
import com.drew.request.ForgotPasswordTokenRequest;
import com.drew.entity.ForgotPasswordToken;
import com.drew.entity.User;
import com.drew.entity.VerificationType;
import com.drew.request.ResetPasswordRequest;
import com.drew.response.APIResponse;
import com.drew.response.AuthenticationResponse;
import com.drew.service.EmailService;
import com.drew.service.ForgotPasswordService;
import com.drew.service.UserService;
import com.drew.service.VerificationCodeService;
import com.drew.utils.OTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUser(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOTP(@RequestHeader("Authorization") String jwt, @PathVariable VerificationType verificationType) throws Exception {
        User user = userService.findUserByJwt(jwt);

        VerificationCode verCode = verificationCodeService.getVerificationCodeByUserId(user.getId());

        if (verCode == null) {
           verCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        if (verificationType.equals(VerificationType.EMAIL_VERIFICATION)) {
            emailService.sendOTPEmail(user.getEmail(), verCode.getOTP());
        }

        return new ResponseEntity<>("verification OTP sent successfully", HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactor(@PathVariable String otp, @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);

        VerificationCode verCode = verificationCodeService.getVerificationCodeByUserId(user.getId());

        String sendTo = verCode.getVerificationType().equals(VerificationType.EMAIL_VERIFICATION) ? verCode.getEmail() : verCode.getPhoneNumber();

        boolean isAuthenticated = verCode.getOTP().equals(otp);

        if (isAuthenticated) {
            User updateUser = userService.enableTwoFactorAuthentication(user, verCode.getVerificationType(), sendTo);

            verificationCodeService.deleteVerificationCodeByUserId(verCode);
            return new ResponseEntity<>(updateUser, HttpStatus.OK);
        }
        throw new Exception("Entered wrong one-time password");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthenticationResponse> sendForgotPasswordOTP(@RequestBody ForgotPasswordTokenRequest request) throws Exception {

        User user = userService.findUserByEmail(request.getSendTo());
        String otp = OTPUtils.generateOTP();

        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordService.findByUserId(user.getId());

        if (token == null) {
            token = forgotPasswordService.createForgotPasswordToken(user, otp, request.getVerificationType(), request.getSendTo(), id);
        }

        if (request.getVerificationType().equals(VerificationType.EMAIL_VERIFICATION)) {
            emailService.sendOTPEmail(user.getEmail(), token.getOTP());
        }

        if (request.getVerificationType().equals(VerificationType.PHONE_VERIFICATION)) {
            // TODO: phoneService
        }

        AuthenticationResponse response = new AuthenticationResponse();
        response.setSession(token.getId());
        response.setMessage("Password reset OTP sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/auth/users/reset-password/verify-otp/{otp}")
    public ResponseEntity<APIResponse> resetPassword(@RequestParam String id, @RequestBody ResetPasswordRequest request, @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);

        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

        boolean isVerified = forgotPasswordToken.getOTP().equals(request.getOTP());

        if (isVerified) {
            userService.updatePassword(forgotPasswordToken.getUser(), request.getPassword());
            APIResponse apiResponse = new APIResponse();
            apiResponse.setMessage("Password reset successfully");
            return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
        }

        throw new Exception("Entered wrong one-time password");
    }
}
