package com.example.user_service.exception;

public class RedisOperationException extends RuntimeException {
  public RedisOperationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
