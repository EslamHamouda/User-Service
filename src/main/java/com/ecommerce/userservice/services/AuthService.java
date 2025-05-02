package com.ecommerce.userservice.services;

import com.ecommerce.userservice.model.request.LoginRequest;
import com.ecommerce.userservice.model.request.PasswordResetConfirmRequest;
import com.ecommerce.userservice.model.request.PasswordResetRequest;
import com.ecommerce.userservice.model.request.SignupRequest;
import com.ecommerce.userservice.model.response.LoginResponse;

public interface AuthService {
    LoginResponse authenticateUser(LoginRequest loginRequest);

    String registerUser(SignupRequest signUpRequest);

    String resetPassword(PasswordResetRequest resetRequest);

    String resetPasswordConfirm(PasswordResetConfirmRequest confirmRequest);
}
