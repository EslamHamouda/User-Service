package com.ecommerce.userservice.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final int statusCode;

    public BaseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
