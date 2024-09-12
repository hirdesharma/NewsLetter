package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.dto.SubscriptionMessage;
import com.example.user_subscription_service.exception.UserSubscriptionException;
import com.example.user_subscription_service.model.UserSubscription;
import com.example.user_subscription_service.repository.UserSubscriptionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service

public class SubscriptionMessageProcessor implements SubscriptionMessageProcessorInterface {
  private final UserSubscriptionRepository userSubscriptionRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ExternalService externalService;
  @Value("${spring.kafka.topic}")
  private String kafkaTopic;

  @Override
  public final UserSubscription publishKafkaMessage(final Subscription subscription,
                                                    final UserSubscription userSubscription) {
    final long duration = subscription.getDuration();
    final LocalDate now = LocalDate.now();

    userSubscription.setDuration(duration);
    userSubscription.setStartDate(now);
    userSubscription.setEndDate(LocalDate.now().plusDays(duration));
    userSubscription.setUpdatedAt(now);
    userSubscription.setIsActive(true);

    SubscriptionMessage subscriptionMessage =
        externalService.fetchSubscriptionMessage(userSubscription.getUserId());

    try {
      final ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      final String messageJson = objectMapper.writeValueAsString(subscriptionMessage);
      System.out.println("Successfully serialized: " + messageJson);
      kafkaTemplate.send(kafkaTopic, messageJson);
    } catch (JsonProcessingException e) {
      System.out.println("Error serializing SubscriptionMessage: " + e.getMessage());
      throw new UserSubscriptionException("Error serializing SubscriptionMessage: ", e);
    }

    return userSubscriptionRepository.save(userSubscription);
  }
}
