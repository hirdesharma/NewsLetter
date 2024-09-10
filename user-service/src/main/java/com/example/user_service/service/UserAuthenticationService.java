package com.example.user_service.service;

import com.example.user_service.jwtconfig.JwtUtil;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserAuthenticationServiceInterface {
  private final UserRepository userRepository;
  private final RedisService redisService;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public final User authenticate(final String email, final String password) {
    //    final String cacheKey = "user:" + email;
    //    Optional<User> cachedUser = redisService.get(cacheKey, User.class);
    //
    //    User user = cachedUser.orElseGet(() -> userRepository.findByEmail(email)
    //        .orElseThrow(() -> new RuntimeException("User not found")));
    User user = userRepository.findByEmail(email).get();

    if (passwordEncoder.matches(password, user.getPassword())) {
      String token = jwtUtil.generateToken(user.getId(), user.getRole());
      user.setJwtToken(token);
      //      redisService.set(cacheKey, user, 6000L);

      return user;
    } else {
      throw new RuntimeException("Invalid credentials");
    }
  }
}
