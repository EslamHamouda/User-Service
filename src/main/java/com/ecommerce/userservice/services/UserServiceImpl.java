package com.ecommerce.userservice.services;

import com.ecommerce.userservice.entity.RoleEntity;
import com.ecommerce.userservice.exception.ResourceAlreadyExistException;
import com.ecommerce.userservice.exception.ResourceNotFoundException;
import com.ecommerce.userservice.mapper.UserMapper;
import com.ecommerce.userservice.entity.UserEntity;
import com.ecommerce.userservice.dto.request.ProfileDtoRequest;
import com.ecommerce.userservice.dto.response.ProfileDtoResponse;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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

    @Override
    public List<ProfileDtoResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProfileDtoResponse getUserById(Long id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return userMapper.toProfileResponse(userOptional.get());
    }

    @Override
    public String deleteUser(Long id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return "User deleted successfully!";
    }

    @Override
    public String addRoleToUser(Long id, String role) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        RoleEntity roleEntity = roleRepository.findByName(role)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + role));

        if (user.getRoles().contains(roleEntity)) {
            throw new ResourceAlreadyExistException("Role already exist with name: " + role + " for user with id: " + id);
        }
        user.getRoles().add(roleEntity);
        userRepository.save(user);
        return "Role " + role + " assigned to user successfully";
    }

    @Override
    public String removeRoleFromUser(Long id, String role) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        RoleEntity roleEntity = roleRepository.findByName(role)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + role));

        if (!user.getRoles().contains(roleEntity)) {
            throw new ResourceNotFoundException("Role not found with name: " + role + " for user with id: " + id);
        }
        user.getRoles().remove(roleEntity);
        userRepository.save(user);
        return "Role " + role + " removed from user successfully";
    }

    @Override
    public Set<String> getUserRoles(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
    }
}
