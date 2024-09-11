package com.example.user_subscription_service.configure;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(5))  // Connection timeout
        .setReadTimeout(Duration.ofSeconds(5))     // Read timeout
        .build();
  }
}
