package com.ecommerce.userservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceExpiredException extends BaseException {
    public ResourceExpiredException(String message) {
        super(message, HttpStatus.FORBIDDEN.value());
    }
}