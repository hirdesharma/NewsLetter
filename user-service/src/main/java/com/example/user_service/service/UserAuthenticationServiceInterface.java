package com.example.user_service.service;

import com.example.user_service.model.User;

public interface UserAuthenticationServiceInterface {
  User authenticate(String email, String password);
}
