package com.ecommerce.userservice.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class MessageResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    public MessageResponse(int status, String message) {
        this.message = message;
        this.status = status;
    }
}