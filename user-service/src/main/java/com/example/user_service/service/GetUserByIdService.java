package com.example.user_service.service;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserByIdService implements GetUserByIdServiceInterface {
  private final UserRepository userRepository;
  private final RedisService redisService;

  public final User getUserById(final Long id) {
    final String cacheKey = "user:" + id;
    Optional<User> cachedUser = redisService.get(cacheKey, User.class);

    if (cachedUser.isPresent()) {
      return cachedUser.get();
    }
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

    redisService.set(cacheKey, user, 6000L);

    return user;
  }

}
