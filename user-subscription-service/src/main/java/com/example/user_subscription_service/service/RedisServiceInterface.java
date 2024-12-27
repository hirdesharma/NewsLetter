package com.example.user_subscription_service.service;

import java.util.Optional;

public interface RedisServiceInterface {

  <T> Optional<T> get(String key, Class<T> entityClass);

  void set(String key, Object obj, Long ttl);
}
