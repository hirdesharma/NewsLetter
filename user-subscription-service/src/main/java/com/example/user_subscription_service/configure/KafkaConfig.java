package com.example.user_subscription_service.configure;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
  @Value("${spring.kafka.topic}")
  private String kafkaTopic;
  @Bean
  public NewTopic userMail() {
    return TopicBuilder.name(kafkaTopic).build();
  }
}
