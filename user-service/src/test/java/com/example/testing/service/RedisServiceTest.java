package com.example.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.user_service.exception.RedisOperationException;
import com.example.user_service.model.User;
import com.example.user_service.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class RedisServiceTest {

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private RedisService redisService;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(1L);
    user.setEmail("hirdesh.sharma@example.com");
    user.setPassword("password123");

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  void testGet_Success() throws Exception {
    String key = "user:1";
    String jsonValue = "{\"id\":1,\"email\":\"hirdesh.sharma@example.com\",\"password\":\"password123\"}";

    when(valueOperations.get(eq(key))).thenReturn(jsonValue);
    when(objectMapper.readValue(eq(jsonValue), eq(User.class))).thenReturn(user);

    Optional<User> result = redisService.get(key, User.class);

    assertTrue(result.isPresent());
    assertEquals(user, result.get());
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).get(eq(key));
    verify(objectMapper, times(1)).readValue(eq(jsonValue), eq(User.class));
  }

  @Test
  void testGet_NotFound() {
    String key = "user:2";
    when(valueOperations.get(eq(key))).thenReturn(null);

    Optional<User> result = redisService.get(key, User.class);

    assertFalse(result.isPresent());
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).get(eq(key));
    verifyNoMoreInteractions(objectMapper);
  }

  @Test
  void testGet_Exception() {
    String key = "user:3";
    when(valueOperations.get(eq(key))).thenThrow(new RuntimeException("Redis error"));

    assertThrows(RedisOperationException.class, () -> redisService.get(key, User.class));
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).get(eq(key));
    verifyNoMoreInteractions(objectMapper);
  }

  @Test
  void testSet_Success() throws Exception {
    String key = "user:1";
    Long ttl = 6000L;
    String jsonValue = "{\"id\":1,\"email\":\"hirdesh.sharma@example.com\",\"password\":\"password123\"}";

    when(objectMapper.writeValueAsString(eq(user))).thenReturn(jsonValue);

    redisService.set(key, user, ttl);

    verify(objectMapper, times(1)).writeValueAsString(eq(user));
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).set(eq(key), eq(jsonValue), eq(ttl), eq(TimeUnit.SECONDS));
  }

  @Test
  void testSet_Exception() throws Exception {
    String key = "user:1";
    Long ttl = 6000L;
    when(objectMapper.writeValueAsString(eq(user))).thenThrow(new RuntimeException("Serialization error"));

    assertThrows(RedisOperationException.class, () -> redisService.set(key, user, ttl));

    verify(objectMapper, times(1)).writeValueAsString(eq(user));
    verifyNoMoreInteractions(valueOperations);
  }
}
