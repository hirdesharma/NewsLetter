package com.example.user_service.service;

import com.example.user_service.model.User;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthServiceInterface {

  @Override
  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
      throw new RuntimeException("User is not authenticated");
    }
    return (User) authentication.getPrincipal();
  }

  @Override
  public void validateUserAccess(User authenticatedUser, Long userId) {
    if (!authenticatedUser.getId().equals(userId)) {
      throw new RuntimeException("Access denied: You can only access your own data.");
    }
  }
}
