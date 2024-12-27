package com.example.user_service.controller;

import com.example.user_service.dto.UserDto;
import com.example.user_service.jwtconfig.JwtUtil;
import com.example.user_service.model.User;
import com.example.user_service.service.AuthService;
import com.example.user_service.service.UserAuthenticationServiceInterface;
import com.example.user_service.service.UserRegistrationServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final UserAuthenticationServiceInterface userAuthenticationService;
  private final AuthService authService;
  private final JwtUtil jwtUtil;

  /**
   * Registers a new user.
   *
   * @param user the user to register
   * @return the registered user
   */
  @Override
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.OK)
  public final User register(@Valid @RequestBody final User user) {
    return userRegistrationService.registerUser(user);
  }

  /**
   * Authenticates a user.
   *
   * @param user the user to authenticate
   * @return the authenticated user
   */
  @Override
  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public final ResponseEntity<UserDto> login(@Valid @RequestBody final User user) {
    UserDto authenticatedUser = userAuthenticationService.authenticate(user.getEmail(),
        user.getPassword());
    String token = jwtUtil.generateToken(authenticatedUser.getId(), authenticatedUser.getRole());

    return ResponseEntity.ok()
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .body(authenticatedUser);
  }

  /**
   * Retrieves a user by their id.
   *
   * @param userId the user to retrieve
   * @return the user
   */
  @Override
  @GetMapping("/user/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public final User getUser(@Valid @PathVariable final Long userId) {
    User authenticatedUser = authService.getAuthenticatedUser();
    authService.validateUserAccess(authenticatedUser, userId);
    return authenticatedUser;
  }

  /**
   * Retrieves the currently authenticated user.
   *
   * @return the authenticated user
   * @throws RuntimeException if the user is not authenticated
   */
  @GetMapping("/auth")
  @ResponseStatus(HttpStatus.OK)
  @Override
  public User getCurrentUser() {
    return authService.getAuthenticatedUser();
  }
}
