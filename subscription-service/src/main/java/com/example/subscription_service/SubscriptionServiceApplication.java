package com.example.subscription_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SubscriptionServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(SubscriptionServiceApplication.class, args);
  }
}

