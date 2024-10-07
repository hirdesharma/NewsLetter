package com.example.user_service.service;

import com.example.user_service.model.User;

public interface GetUserByIdServiceInterface {
  
  User getUserById(Long id);
}
