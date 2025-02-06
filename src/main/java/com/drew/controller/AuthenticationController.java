package com.drew.controller;

import com.drew.auth.TwoFactorOTP;
import com.drew.config.JwtProvider;
import com.drew.entity.User;
import com.drew.repository.UserRepository;
import com.drew.response.AuthenticationResponse;
import com.drew.service.CustomUserDetailsService;
import com.drew.service.EmailService;
import com.drew.service.TwoFactorOTPService;
import com.drew.utils.OTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOTPService twoFactorOTPService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> createUser(@RequestBody User user) throws Exception {

        User doesEmailExist = userRepository.findByEmail(user.getEmail());
        if (doesEmailExist != null) {
            throw new Exception("An account with this email already exists");
        }
        // else we create a new user

        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());

        User savedUser = userRepository.save(newUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("Account created successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody User user) throws Exception {

        String username = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(username, password);
        
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        // username is equiv to email
        User authUser = userRepository.findByEmail(username);

        // IsEnabled()... @Data lombok so getIsEnabled()
        // from TwoFactorAuthentication.java
        if (user.getTwoFactorAuthentication().getIsEnabled()) {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setMessage("Two factor authentication is enabled");
            response.setTwoFactorAuthEnabled(true);
            String otp = OTPUtils.generateOTP();

            TwoFactorOTP oldOTP = twoFactorOTPService.findByUserId(authUser.getId());

            if (oldOTP != null) {
                twoFactorOTPService.deleteTwoFactorOTP(oldOTP);
            }

            TwoFactorOTP newOTP = twoFactorOTPService.createTwoFactorOTP(authUser, otp, jwt);

            emailService.sendOTPEmail(username, otp);

            response.setSession(newOTP.getId());
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        // if TwoFactorAuth is not enabled you will be signed in if you provided correct credentials
        AuthenticationResponse response = new AuthenticationResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("Login successful");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("No user found with username: " + username);
        }

        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("The password you entered is incorrect");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthenticationResponse> verifySignInOTP(@PathVariable String otp, @RequestParam String id) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);

        if (twoFactorOTPService.verifyTwoFactorOTP(twoFactorOTP, otp)) {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setMessage("Two factor authentication is verified");
            response.setTwoFactorAuthEnabled(true);
            response.setJwt(twoFactorOTP.getJwtToken());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        throw new Exception("Invalid OTP");
    }
}
