package com.example.user_subscription_service.context;

import org.springframework.stereotype.Service;

@Service
public class JwtContextHolder {
  private static final ThreadLocal<String> jwtTokenHolder = new ThreadLocal<>();

  public static void setJwtToken(String token) {
    jwtTokenHolder.set(token);
  }

  public static String getJwtToken() {
    return jwtTokenHolder.get();
  }

  public static void clear() {
    jwtTokenHolder.remove();
  }
}