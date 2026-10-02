package com.noteiq.service;

import com.noteiq.dto.user.UserResponse;
import com.noteiq.entity.User;
import com.noteiq.exception.ResourceNotFoundException;
import com.noteiq.exception.UnauthorizedException;
import com.noteiq.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public UserResponse getCurrentUserProfile() {
        User user = getCurrentAuthenticatedUser();
        return UserResponse.fromEntity(user);
    }
}
