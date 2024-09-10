package com.example.user_service.service;

import com.example.user_service.exception.RedisOperationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService implements RedisServiceInterface {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  public final <T> Optional<T> get(final String key, final Class<T> entityClass) {
    try {
      final String json = redisTemplate.opsForValue().get(key);
      if (Objects.nonNull(json)) {
        return Optional.of(objectMapper.readValue(json, entityClass));
      }
      return Optional.empty();
    } catch (Exception e) {
      throw new RedisOperationException("Failed to get data from Redis", e);
    }
  }

  public final void set(final String key, final Object obj, final Long ttl) {
    try {
      final String jsonValue = objectMapper.writeValueAsString(obj);
      redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
    } catch (Exception e) {
      throw new RedisOperationException("Failed to set data in Redis", e);
    }
  }
}