package com.ecommerce.userservice.services;

import com.ecommerce.userservice.dto.request.ProfileDtoRequest;
import com.ecommerce.userservice.dto.response.ProfileDtoResponse;
import java.util.List;
import java.util.Set;

public interface UserService {
    ProfileDtoResponse getUserProfile();

    String updateUserProfile(ProfileDtoRequest profileDtoRequest);

    List<ProfileDtoResponse> getAllUsers();

    ProfileDtoResponse getUserById(Long id);

    String deleteUser(Long id);

    String addRoleToUser(Long id, String role);

    String removeRoleFromUser(Long id, String role);

    Set<String> getUserRoles(Long id);
}
