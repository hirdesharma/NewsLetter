package com.example.user_service.controller;

import com.example.user_service.jwtconfig.JwtUtil;
import com.example.user_service.model.User;
import com.example.user_service.service.GetUserByIdServiceInterface;
import com.example.user_service.service.UserAuthenticationServiceInterface;
import com.example.user_service.service.UserRegistrationServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserControllerInterface {
  private final UserRegistrationServiceInterface userRegistrationService;
  private final GetUserByIdServiceInterface getUserByIdService;
  private final UserAuthenticationServiceInterface userAuthenticationService;
  private final JwtUtil jwtUtil;

  @Override
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.OK)
  public final User register(@Valid @RequestBody final User user) {
    return userRegistrationService.registerUser(user);
  }

  @Override
  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public final User login(@Valid @RequestBody final User user) {
    return userAuthenticationService.authenticate(user.getEmail(), user.getPassword());
  }

  @Override
  @GetMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public final User getUser(@Valid @PathVariable final Long userId) {
    return getUserByIdService.getUserById(userId);
  }

  @GetMapping("/auth")
  @ResponseStatus(HttpStatus.OK)
  @Override
  public boolean getCurrentUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      System.out.println("Authenticated user: " + authentication.getPrincipal());
      return true;
    } else {
      throw new RuntimeException("User is not authenticated");
    }
  }
}
