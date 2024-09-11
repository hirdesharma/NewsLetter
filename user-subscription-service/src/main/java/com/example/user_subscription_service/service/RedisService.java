package com.example.user_subscription_service.service;

import com.example.user_subscription_service.exception.RedisOperationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService implements RedisServiceInterface {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public RedisService(final RedisTemplate<String, String> redisTemplate,
                      final ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public final <T> Optional<T> get(final String key, final Class<T> entityClass) {
    try {
      final String json = redisTemplate.opsForValue().get(key);
      if (Objects.nonNull(json)) {
        return Optional.of(objectMapper.readValue(json, entityClass));
      }
      return Optional.empty();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new RedisOperationException("Failed to get data from Redis", e);
    }
  }

  @Override
  public final void set(final String key, final Object obj, final Long ttl) {
    try {
      final String jsonValue = objectMapper.writeValueAsString(obj);
      redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
    } catch (Exception e) {
      throw new RedisOperationException("Failed to set data in Redis", e);
    }
  }
}