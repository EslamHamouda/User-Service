package com.ecommerce.userservice.services;

import com.ecommerce.userservice.dto.request.LoginDtoRequest;
import com.ecommerce.userservice.dto.request.PasswordResetConfirmDtoRequest;
import com.ecommerce.userservice.dto.request.PasswordResetDtoRequest;
import com.ecommerce.userservice.dto.request.SignupDtoRequest;
import com.ecommerce.userservice.dto.response.LoginDtoResponse;

public interface AuthService {
    LoginDtoResponse authenticateUser(LoginDtoRequest loginDtoRequest);

    String registerUser(SignupDtoRequest signUpDtoRequest);

    String resetPassword(PasswordResetDtoRequest resetRequest);

    String resetPasswordConfirm(PasswordResetConfirmDtoRequest confirmRequest);

    String refreshToken(String refreshToken);
}
