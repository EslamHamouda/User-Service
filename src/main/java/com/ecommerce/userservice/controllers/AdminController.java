package com.ecommerce.userservice.controllers;

import com.ecommerce.userservice.dto.response.GenericResponse;
import com.ecommerce.userservice.dto.response.ProfileDtoResponse;
import com.ecommerce.userservice.services.UserService;
import com.ecommerce.userservice.utils.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import static org.springframework.http.HttpStatus.OK;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<GenericResponse<List<ProfileDtoResponse>>> getAllUsers() {
        List<ProfileDtoResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), MessageConstants.USERS_RETRIEVED, users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<GenericResponse<ProfileDtoResponse>> getUserById(@PathVariable Long id) {
        ProfileDtoResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), MessageConstants.USER_RETRIEVED, user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<GenericResponse<String>> deleteUser(@PathVariable Long id) {
        String message = userService.deleteUser(id);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(), message, null));
    }

    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<GenericResponse<String>> addRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        String message = userService.addRoleToUser(userId, roleName);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(),
                message, null));
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<GenericResponse<String>> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        String message = userService.removeRoleFromUser(userId, roleName);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(),
                message, null));
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<GenericResponse<Set<String>>> getUserRoles(@PathVariable Long userId) {
        Set<String> roles = userService.getUserRoles(userId);
        return ResponseEntity.ok(new GenericResponse<>(OK.value(),
                MessageConstants.ROLES_RETRIEVED, roles));
    }
}
