package com.ecommerce.userservice.controllers;

import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.model.response.MessageResponse;
import com.ecommerce.userservice.model.response.ProfileResponse;
import com.ecommerce.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            User user = userService.getUserProfile();

            if (user.getId() != null) {
                return ResponseEntity.ok(new ProfileResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhone()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody User userUpdates) {
        try {
            String message = userService.updateUserProfile(userUpdates);
            if (!message.isBlank()) {
                return ResponseEntity.ok(new MessageResponse(message));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/test/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/test/user")
    public String userAccess() {
        return "User Content.";
    }
}
