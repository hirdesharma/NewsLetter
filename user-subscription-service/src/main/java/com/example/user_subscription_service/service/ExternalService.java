package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.dto.SubscriptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExternalService implements ExternalServiceInterface {

  private final RestTemplate restTemplate;

  public final Subscription fetchSubscription(final Long subscriptionId) {
    try {
      return restTemplate.getForObject(
          "http://localhost:8081/api/subscriptions/" + subscriptionId,
          Subscription.class);
    } catch (RestClientException e) {
      throw new RuntimeException("Failed to fetch subscription details", e);
    }
  }

  public final SubscriptionMessage fetchSubscriptionMessage(final Long userId) {
    try {
      return restTemplate.getForObject(
          "http://localhost:8080/api/users/" + userId,
          SubscriptionMessage.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to fetch user details", e);
    }
  }
}
