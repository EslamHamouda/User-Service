package com.ecommerce.userservice.exception;

import org.springframework.http.HttpStatus;

public class BadCredentialsException extends BaseException {

    public BadCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value());
    }
}
