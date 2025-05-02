package com.ecommerce.userservice.services;

import com.ecommerce.userservice.model.User;

public interface UserService {
    User getUserProfile();

    String updateUserProfile(User userUpdates);
}
