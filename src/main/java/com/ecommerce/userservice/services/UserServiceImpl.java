package com.ecommerce.userservice.services;

import com.ecommerce.userservice.exception.ResourceNotFoundException;
import com.ecommerce.userservice.mapper.UserMapper;
import com.ecommerce.userservice.entity.UserEntity;
import com.ecommerce.userservice.dto.request.ProfileDtoRequest;
import com.ecommerce.userservice.dto.response.ProfileDtoResponse;
import com.ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ProfileDtoResponse getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();

        Optional<UserEntity> user = userRepository.findById(userDetails.getId());

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found!");
        }

        return userMapper.toProfileResponse(user.get());
    }

    @Override
    public String updateUserProfile(ProfileDtoRequest profileDtoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();

        Optional<UserEntity> userOptional = userRepository.findById(userDetails.getId());

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found!");
        }

        UserEntity user = userOptional.get();

        if (profileDtoRequest.getFirstName() != null) {
            user.setFirstName(profileDtoRequest.getFirstName());
        }
        if (profileDtoRequest.getLastName() != null) {
            user.setLastName(profileDtoRequest.getLastName());
        }
        if (profileDtoRequest.getPhone() != null) {
            user.setPhone(profileDtoRequest.getPhone());
        }

        userRepository.save(user);
        return "Profile updated successfully!";
    }
}
