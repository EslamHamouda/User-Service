package com.ecommerce.userservice.services;

import com.ecommerce.userservice.dto.request.ProfileDtoRequest;
import com.ecommerce.userservice.dto.response.ProfileDtoResponse;

import java.util.List;

public interface UserService {
    ProfileDtoResponse getUserProfile();

    String updateUserProfile(ProfileDtoRequest profileDtoRequest);

    List<ProfileDtoResponse> getAllUsers();

    ProfileDtoResponse getUserById(Long id);

    String deleteUser(Long id);
}
