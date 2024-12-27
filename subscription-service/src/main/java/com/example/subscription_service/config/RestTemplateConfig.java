package com.example.subscription_service.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Value("${spring.restTemplate.timeout.duration.connect}")
  private long connectTimeout;
  @Value("${spring.restTemplate.timeout.duration.read}")
  private long readTimeout;

  @Bean
  public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(connectTimeout))  // Connection timeout
        .setReadTimeout(Duration.ofSeconds(readTimeout))     // Read timeout
        .build();
  }
}
