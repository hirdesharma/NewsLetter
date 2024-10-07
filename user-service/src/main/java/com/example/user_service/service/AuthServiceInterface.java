package com.example.user_service.service;

import com.example.user_service.model.User;

public interface AuthServiceInterface {
  User getAuthenticatedUser();

  void validateUserAccess(User authenticatedUser, Long userId);
}
