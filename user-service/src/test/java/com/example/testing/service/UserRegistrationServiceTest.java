package com.example.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.RedisService;
import com.example.user_service.service.UserRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserRegistrationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RedisService redisService;

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserRegistrationService userRegistrationService;

  @Value("${admin.email}")
  private String adminEmail;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(1L);
    user.setEmail("hirdesh.sharma@example.com");
    user.setPassword("password123");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void testRegisterUser_Success() {
    String adminEmail = "sharmahirdesh001@gmail.com";
    user.setEmail(adminEmail);

    when(userRepository.findByEmail(eq(user.getEmail()))).thenReturn(Optional.empty());
    when(passwordEncoder.encode(eq(user.getPassword()))).thenReturn("password123");
    doNothing().when(redisService).set(anyString(), any(User.class), anyLong());

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    when(kafkaTemplate.send(eq("registrationNotify"), anyString())).thenReturn(null);

    User result = userRegistrationService.registerUser(user);

    assertNotNull(result);
    assertEquals("password123", result.getPassword());
    assertEquals("ROLE_USER", result.getRole());
    verify(userRepository, times(1)).findByEmail(eq(user.getEmail()));
    verify(redisService, times(1)).set(anyString(), eq(user), eq(3600L));
    //    verify(kafkaTemplate, times(1)).send(eq("registrationNotify"), eq(messageJson));
    verify(userRepository, times(1)).save(eq(user));
  }

  @Test
  void testRegisterUser_UserAlreadyRegistered() {
    when(userRepository.findByEmail(eq(user.getEmail()))).thenReturn(Optional.of(user));

    RuntimeException thrownException = assertThrows(RuntimeException.class,
        () -> userRegistrationService.registerUser(user));

    assertEquals("User with email hirdesh.sharma@example.com is already registered.",
        thrownException.getMessage());
    verify(userRepository, times(1)).findByEmail(eq(user.getEmail()));
    verifyNoInteractions(redisService, kafkaTemplate, passwordEncoder);
  }
}
