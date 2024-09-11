package com.example.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.user_service.jwtconfig.JwtUtil;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserAuthenticationService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserAuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private UserAuthenticationService userAuthenticationService;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(1L);
    user.setEmail("hirdesh.sharma@example.com");
    user.setPassword("password123");
    user.setRole("ROLE_USER");
  }

  @Test
  void testAuthenticate_Success() {
    String email = "hirdesh.sharma@example.com";
    String password = "password123";
    String token = "jwtToken";

    when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(eq(password), eq(user.getPassword()))).thenReturn(true);
    when(jwtUtil.generateToken(eq(user.getId()), eq(user.getRole()))).thenReturn(token);

    User result = userAuthenticationService.authenticate(email, password);

    assertNotNull(result);
    assertEquals(token, result.getJwtToken());
    assertEquals(user, result);
    verify(userRepository, times(1)).findByEmail(eq(email));
    verify(passwordEncoder, times(1)).matches(eq(password), eq(user.getPassword()));
    verify(jwtUtil, times(1)).generateToken(eq(user.getId()), eq(user.getRole()));
  }

  @Test
  void testAuthenticate_InvalidCredentials() {
    String email = "hirdesh.sharma@example.com";
    String password = "wrongPassword";

    when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(eq(password), eq(user.getPassword()))).thenReturn(false);

    RuntimeException thrownException = assertThrows(RuntimeException.class,
        () -> userAuthenticationService.authenticate(email, password));

    assertEquals("Invalid credentials", thrownException.getMessage());
    verify(userRepository, times(1)).findByEmail(eq(email));
    verify(passwordEncoder, times(1)).matches(eq(password), eq(user.getPassword()));
    verifyNoInteractions(jwtUtil);
  }

  @Test
  void testAuthenticate_UserNotFound() {
    String email = "hirdesh.sharma@example.com";
    String password = "password123";

    when(userRepository.findByEmail(eq(email))).thenReturn(Optional.empty());

    RuntimeException thrownException = assertThrows(RuntimeException.class,
        () -> userAuthenticationService.authenticate(email, password));

    assertEquals("Invalid credentials", thrownException.getMessage());
    verify(userRepository, times(1)).findByEmail(eq(email));
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(jwtUtil);
  }
}
