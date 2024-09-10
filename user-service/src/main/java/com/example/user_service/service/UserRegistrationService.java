package com.example.user_service.service;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService implements UserRegistrationServiceInterface {

  private final UserRepository userRepository;
  private final RedisService redisService;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final PasswordEncoder passwordEncoder;

  public final User registerUser(final User user) {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      // Handle already registered user case
      System.out.println(
          "Registration denied: User with email " + user.getEmail() + " already exists.");
      throw new RuntimeException("User with email " + user.getEmail() + " is already registered.");
    }
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    final String cacheKey = "user:" + user.getEmail();
    redisService.set(cacheKey, user, 3600L);

    User savedUser = userRepository.save(user);

    // Send Kafka message upon successful registration
    kafkaTemplate.send("registrationNotify", "User registered successfully: " + user.getEmail());
    //    return savedUser;
    user.setRole("ROLE_USER");
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    userRepository.save(user);

    return user;
  }
}
