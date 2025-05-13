package com.ecommerce.userservice.utils;

public final class MessageConstants {
    private MessageConstants() {}
    public static final String INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String USERNAME_TAKEN = "Username is already taken!";
    public static final String EMAIL_IN_USE = "Email is already in use!";
    public static final String ROLE_NOT_FOUND = "Role USER is not found.";
    public static final String USER_REGISTERED = "User registered successfully!";
    public static final String USER_DELETED = "User deleted successfully!";
    public static final String PASSWORD_RESET_SENT = "Password reset link has been sent to your email. Token: ";
    public static final String PASSWORD_RESET_SUCCESS = "Password has been reset successfully.";
    public static final String INVALID_PASSWORD_RESET_TOKEN = "Invalid password reset token or expired.";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token or expired.";
    public static final String TOKEN_REFRESHED = "Token refreshed successfully";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String PASSWORD_RESET_TOKEN_EXPIRED = "Password reset token has expired.";
    public static final String EMAIL_RESET_MESSAGE = "If your email exists in our system, you will receive a password reset link.";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred: ";
    public static final String PROFILE_UPDATED = "Profile updated successfully!";
    public static final String ROLE_ALREADY_EXISTS = "Role already exist with name: %s for user with id: %d";
    public static final String ROLE_ASSIGNED = "Role %s assigned to user successfully";
    public static final String USER_NOT_FOUND_WITH_ID = "User not found with id: %d";
    public static final String ROLE_NOT_FOUND_WITH_NAME = "Role not found with name: %s";
    public static final String ROLE_NOT_FOUND_FOR_USER = "Role not found with name: %s for user with id: %d";
    public static final String ROLE_REMOVED = "Role %s removed from user successfully";
    public static final String USER_NOT_FOUND_WITH_USERNAME = "User Not Found with username: %s";
    public static final String INVALID_JWT_TOKEN = "Invalid JWT token: %s";
}
