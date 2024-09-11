package com.example.user_service.service;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${admin.email}")
  private String adminEmail;

  @Override
  public final User registerUser(final User user) {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      System.out.println(
          "Registration denied: User with email " + user.getEmail() + " already exists.");
      throw new RuntimeException("User with email " + user.getEmail() + " is already registered.");
    }
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    System.out.println(adminEmail + " " + user.getEmail() + " hii");
    if (user.getEmail().equals(adminEmail)) {
      user.setRole("ROLE_ADMIN");
    }
    final String cacheKey = "user:" + user.getEmail();
    redisService.set(cacheKey, user, 3600L);

    return encodePasswordAndSaveUser(user);
  }

  private User encodePasswordAndSaveUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      String messageJson = objectMapper.writeValueAsString(user);
      System.out.println("Successfully serialized: " + messageJson);
      kafkaTemplate.send("registrationNotify", messageJson);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      System.out.println("Error serializing SubscriptionMessage: " + e.getMessage());
    }
    userRepository.save(user);

    return user;
  }
}
