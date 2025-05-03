package com.ecommerce.userservice.services;

import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();

        Optional<User> user = userRepository.findById(userDetails.getId());

        return user.orElseGet(User::new);
    }

    @Override
    public String updateUserProfile(User userUpdates) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();

        Optional<User> userOptional = userRepository.findById(userDetails.getId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Update only allowed fields (not username, email, or password)
            if (userUpdates.getFirstName() != null) {
                user.setFirstName(userUpdates.getFirstName());
            }
            if (userUpdates.getLastName() != null) {
                user.setLastName(userUpdates.getLastName());
            }
            if (userUpdates.getPhone() != null) {
                user.setPhone(userUpdates.getPhone());
            }

            userRepository.save(user);
            return "Profile updated successfully!";
        }
            return "";
    }
}