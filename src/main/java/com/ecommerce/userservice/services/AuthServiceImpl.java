package com.ecommerce.userservice.services;

import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.model.request.LoginRequest;
import com.ecommerce.userservice.model.request.PasswordResetConfirmRequest;
import com.ecommerce.userservice.model.request.PasswordResetRequest;
import com.ecommerce.userservice.model.request.SignupRequest;
import com.ecommerce.userservice.model.response.LoginResponse;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user =(User) authentication.getPrincipal();

        return new LoginResponse(jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone());
    }

    @Override
    public String registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return "Error: Username is already taken!";
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return "Error: Email is already in use!";
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getPhone());

        userRepository.save(user);
        return "User registered successfully!";
    }

    @Override
    public String resetPassword(PasswordResetRequest resetRequest) {
        Optional<User> userOptional = userRepository.findByEmail(resetRequest.getEmail());

        if (userOptional.isEmpty()) {
            // For security reasons, don't reveal that the email doesn't exist
            return "If your email exists in our system, you will receive a password reset link.";
        }

        User user = userOptional.get();

        // Generate a random token
        String token = UUID.randomUUID().toString();

        // Set token expiration to 24 hours from now
        Date expiryDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        // Save the token and expiry date to the user
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(expiryDate);
        userRepository.save(user);

        return "Password reset link has been sent to your email. Token: " + token;
    }

    @Override
    public String resetPasswordConfirm(PasswordResetConfirmRequest confirmRequest) {
        Optional<User> userOptional = userRepository.findByResetPasswordToken(confirmRequest.getToken());

        if (userOptional.isEmpty()) {
            return "Invalid or expired token.";
        }

        User user = userOptional.get();

        // Check if token is expired
        if (user.getResetPasswordTokenExpiry() == null ||
                user.getResetPasswordTokenExpiry().before(new Date())) {
            return "Token has expired.";
        }

        // Update password
        user.setPassword(encoder.encode(confirmRequest.getPassword()));

        // Clear the reset token and expiry
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
        return "Password has been reset successfully.";
    }
}
