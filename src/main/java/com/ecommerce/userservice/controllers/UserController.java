package com.ecommerce.userservice.controllers;

import com.ecommerce.userservice.dto.request.ProfileDtoRequest;
import com.ecommerce.userservice.dto.response.GenericResponse;
import com.ecommerce.userservice.dto.response.ProfileDtoResponse;
import com.ecommerce.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<GenericResponse<ProfileDtoResponse>> getUserProfile() {
        ProfileDtoResponse user = userService.getUserProfile();
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), null, user));
    }

    @PutMapping("/profile")
    public ResponseEntity<GenericResponse<String>> updateUserProfile(@RequestBody ProfileDtoRequest profileDtoRequest) {
        String message = userService.updateUserProfile(profileDtoRequest);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), message, null));
    }

}
