package com.ecommerce.userservice.controllers;

import com.ecommerce.userservice.dto.request.LoginDtoRequest;
import com.ecommerce.userservice.dto.request.PasswordResetConfirmDtoRequest;
import com.ecommerce.userservice.dto.request.PasswordResetDtoRequest;
import com.ecommerce.userservice.dto.request.SignupDtoRequest;
import com.ecommerce.userservice.dto.response.GenericResponse;
import com.ecommerce.userservice.dto.response.LoginDtoResponse;
import com.ecommerce.userservice.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<GenericResponse<LoginDtoResponse>> authenticateUser(@Valid @RequestBody LoginDtoRequest loginDtoRequest) {
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), null, authService.authenticateUser(loginDtoRequest)));
    }

    @PostMapping("/signup")
    public ResponseEntity<GenericResponse<String>> registerUser(@Valid @RequestBody SignupDtoRequest signUpDtoRequest) {
        String message = authService.registerUser(signUpDtoRequest);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), message, null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse<String>> resetPassword(@Valid @RequestBody PasswordResetDtoRequest resetRequest) {
        String message = authService.resetPassword(resetRequest);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), message, null));
    }

    @PostMapping("/reset-password-confirm")
    public ResponseEntity<GenericResponse<String>> resetPasswordConfirm(@Valid @RequestBody PasswordResetConfirmDtoRequest confirmRequest) {
        String message = authService.resetPasswordConfirm(confirmRequest);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), message, null));
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<GenericResponse<String>> refreshToken(@RequestParam String refreshToken) {
        String newToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), "Token refreshed successfully", newToken));
    }
}
