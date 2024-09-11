package com.example.user_service.service;

import com.example.user_service.jwtconfig.JwtUtil;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserAuthenticationServiceInterface {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override
  public final User authenticate(final String email, final String password) {
    Optional<User> optUser = userRepository.findByEmail(email);

    if (optUser.isPresent() && passwordEncoder.matches(password, optUser.get().getPassword())) {
      User user = optUser.get();
      String token = jwtUtil.generateToken(user.getId(), user.getRole());
      user.setJwtToken(token);
      return user;
    } else {
      throw new RuntimeException("Invalid credentials");
    }
  }
}
