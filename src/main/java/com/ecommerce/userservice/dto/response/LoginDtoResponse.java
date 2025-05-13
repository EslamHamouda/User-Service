package com.ecommerce.userservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginDtoResponse {
    private String accessToken;
    private String refreshToken;
    private String type;
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public LoginDtoResponse(String accessToken, String refreshToken, Long id, String username, String email, String firstName, String lastName, String phone) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
}
