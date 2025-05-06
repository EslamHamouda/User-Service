package com.ecommerce.userservice.services;

import com.ecommerce.userservice.dto.request.ProfileDtoRequest;
import com.ecommerce.userservice.dto.response.ProfileDtoResponse;

public interface UserService {
    ProfileDtoResponse getUserProfile();

    String updateUserProfile(ProfileDtoRequest profileDtoRequest);
}
