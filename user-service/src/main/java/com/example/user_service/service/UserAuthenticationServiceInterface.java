package com.example.user_service.service;

import com.example.user_service.dto.UserDto;

public interface UserAuthenticationServiceInterface {
  UserDto authenticate(String email, String password);
}
