package com.ecommerce.userservice.services;

import com.ecommerce.userservice.entity.RoleEntity;
import com.ecommerce.userservice.enums.Role;
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
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

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

        UserEntity user = (UserEntity) authentication.getPrincipal();
        String jwt = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        return userMapper.toLoginResponse(user, jwt, refreshToken);
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

        RoleEntity userRole = roleRepository.findByName(Role.USER.name())
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role USER is not found."));
        user.getRoles().add(userRole);

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

        String token = jwtUtils.generatePasswordResetToken(user.getEmail());

        user.setResetPasswordToken(token);

        Date expiryDate = jwtUtils.extractAllClaims(token).getExpiration();
        user.setResetPasswordTokenExpiryDate(expiryDate);

        userRepository.save(user);

        return "Password reset link has been sent to your email. Token: " + token;
    }

    @Override
    public String resetPasswordConfirm(PasswordResetConfirmDtoRequest confirmRequest) {
        String token = confirmRequest.getPasswordResetToken();

        if (!jwtUtils.validateJwtToken(token) || !jwtUtils.isPasswordResetToken(token)) {
            throw new BadCredentialsException("Invalid password reset token.");
        }

        String email = jwtUtils.getUserNameFromJwtToken(token);

        Optional<UserEntity> userOptional = userRepository.findByResetPasswordToken(email);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        UserEntity user = userOptional.get();

        if (user.getResetPasswordToken() == null || !user.getResetPasswordToken().equals(token)) {
            throw new BadCredentialsException("Invalid password reset token.");
        }

        if (user.getResetPasswordTokenExpiryDate() == null ||
                user.getResetPasswordTokenExpiryDate().before(new Date())) {
            throw new BadCredentialsException("Password reset token has expired.");
        }

        user.setPassword(encoder.encode(confirmRequest.getPassword()));

        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiryDate(null);

        userRepository.save(user);
        return "Password has been reset successfully.";
    }

    @Override
    public String refreshToken(String refreshToken) {
        if (!jwtUtils.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        UserEntity user = userOptional.get();

        return jwtUtils.generateAccessToken(user.getUsername());
    }
}
