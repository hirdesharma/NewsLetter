package com.example.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.GetUserByIdService;
import com.example.user_service.service.RedisService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GetUserByIdServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RedisService redisService;

  @InjectMocks
  private GetUserByIdService getUserByIdService;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = new User();
    user.setId(1L);
    user.setEmail("hirdesh.sharma@example.com");
    user.setPassword("password123");
    user.setCreatedAt(LocalDateTime.now().minusDays(1));
    user.setUpdatedAt(LocalDateTime.now());
    user.setRole("ROLE_USER");
    user.setJwtToken("some-jwt-token");
  }

  @Test
  void testGetUserByIdCacheHit() {
    Long userId = 1L;
    when(redisService.get(eq("user:" + userId), eq(User.class)))
        .thenReturn(Optional.of(user));

    User result = getUserByIdService.getUserById(userId);

    assertEquals(user, result);
    verify(redisService, times(1)).get(eq("user:" + userId), eq(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void testGetUserByIdCacheMissUserFound() {
    Long userId = 2L;
    when(redisService.get(eq("user:" + userId), eq(User.class)))
        .thenReturn(Optional.empty());
    when(userRepository.findById(eq(userId)))
        .thenReturn(Optional.of(user));

    User result = getUserByIdService.getUserById(userId);

    assertEquals(user, result);
    verify(redisService, times(1)).get(eq("user:" + userId), eq(User.class));
    verify(userRepository, times(1)).findById(eq(userId));
    verify(redisService, times(1)).set(eq("user:" + userId), eq(user), anyLong());
  }

  @Test
  void testGetUserByIdCacheMissUserNotFound() {
    Long userId = 3L;
    when(redisService.get(eq("user:" + userId), eq(User.class)))
        .thenReturn(Optional.empty());
    when(userRepository.findById(eq(userId)))
        .thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> getUserByIdService.getUserById(userId));
    verify(redisService, times(1)).get(eq("user:" + userId), eq(User.class));
    verify(userRepository, times(1)).findById(eq(userId));
    verify(redisService, never()).set(anyString(), any(User.class), anyLong()); // No cache set
  }
}
