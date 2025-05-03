package com.ecommerce.userservice.controllers;

import com.ecommerce.userservice.model.request.LoginRequest;
import com.ecommerce.userservice.model.request.PasswordResetConfirmRequest;
import com.ecommerce.userservice.model.request.PasswordResetRequest;
import com.ecommerce.userservice.model.request.SignupRequest;
import com.ecommerce.userservice.model.response.MessageResponse;
import com.ecommerce.userservice.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.authenticateUser(loginRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            String message = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Something went wrong!"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        try {
            String message = authService.resetPassword(resetRequest);
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Something went wrong!"));
        }
    }

    @PostMapping("/reset-password-confirm")
    public ResponseEntity<?> resetPasswordConfirm(@Valid @RequestBody PasswordResetConfirmRequest confirmRequest) {
        try {
            String message = authService.resetPasswordConfirm(confirmRequest);
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Something went wrong!"));
        }
    }
}
