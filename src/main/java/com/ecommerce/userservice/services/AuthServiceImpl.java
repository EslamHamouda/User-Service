package com.ecommerce.userservice.services;

import com.ecommerce.userservice.exception.BadCredentialsException;
import com.ecommerce.userservice.exception.DuplicateResourceException;
import com.ecommerce.userservice.exception.ResourceNotFoundException;
import com.ecommerce.userservice.mapper.UserMapper;
import com.ecommerce.userservice.entity.UserEntity;
import com.ecommerce.userservice.dto.request.LoginDtoRequest;
import com.ecommerce.userservice.dto.request.PasswordResetConfirmDtoRequest;
import com.ecommerce.userservice.dto.request.PasswordResetDtoRequest;
import com.ecommerce.userservice.dto.request.SignupDtoRequest;
import com.ecommerce.userservice.dto.response.LoginDtoResponse;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtUtils;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    private final UserMapper userMapper;

    @Override
    public LoginDtoResponse authenticateUser(LoginDtoRequest loginDtoRequest) {
        if (!userRepository.existsByUsername(loginDtoRequest.getUsername())) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDtoRequest.getUsername(), loginDtoRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserEntity user = (UserEntity) authentication.getPrincipal();

        return userMapper.toLoginResponse(user, jwt);
    }

    @Override
    public String registerUser(SignupDtoRequest signUpDtoRequest) {
        if (userRepository.existsByUsername(signUpDtoRequest.getUsername())) {
            throw new DuplicateResourceException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpDtoRequest.getEmail())) {
            throw new DuplicateResourceException("Error: Email is already in use!");
        }

        UserEntity user = new UserEntity(signUpDtoRequest.getUsername(),
                signUpDtoRequest.getEmail(),
                encoder.encode(signUpDtoRequest.getPassword()),
                signUpDtoRequest.getFirstName(),
                signUpDtoRequest.getLastName(),
                signUpDtoRequest.getPhone());

        userRepository.save(user);
        return "User registered successfully!";
    }

    @Override
    public String resetPassword(PasswordResetDtoRequest resetRequest) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(resetRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("If your email exists in our system, you will receive a password reset link.");
        }

        UserEntity user = userOptional.get();

        String token = UUID.randomUUID().toString();

        Date expiryDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(expiryDate);
        userRepository.save(user);

        return "Password reset link has been sent to your email. Token: " + token;
    }

    @Override
    public String resetPasswordConfirm(PasswordResetConfirmDtoRequest confirmRequest) {
        Optional<UserEntity> userOptional = userRepository.findByResetPasswordToken(confirmRequest.getToken());

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Invalid or expired token.");
        }

        UserEntity user = userOptional.get();

        if (user.getResetPasswordTokenExpiry() == null || user.getResetPasswordTokenExpiry().before(new Date())) {
            throw new ValidationException( "Token has expired.");
        }

        user.setPassword(encoder.encode(confirmRequest.getPassword()));

        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
        return "Password has been reset successfully.";
    }
}
