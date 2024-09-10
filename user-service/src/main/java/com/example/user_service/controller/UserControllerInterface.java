package com.example.user_service.controller;

import com.example.user_service.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface UserControllerInterface {
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.OK)
  User register(@RequestBody User user);

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  User login(@RequestBody User user);

  @GetMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  User getUser(@Valid @PathVariable Long userId);

  @GetMapping("/auth")
  @ResponseStatus(HttpStatus.OK)
  boolean getCurrentUser();
}
