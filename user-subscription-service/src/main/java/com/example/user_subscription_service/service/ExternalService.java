package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.dto.SubscriptionMessage;
import com.example.user_subscription_service.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExternalService implements ExternalServiceInterface {

  private final RestTemplate restTemplate;

  @Override
  public final Subscription fetchSubscription(final Long subscriptionId) {
    try {
      return restTemplate.getForObject(
          "http://localhost:8081/api/subscriptions/" + subscriptionId,
          Subscription.class);
    } catch (RestClientException e) {
      throw new RuntimeException("Failed to fetch subscription details", e);
    }
  }

  @Override
  public final SubscriptionMessage fetchSubscriptionMessage(final Long userId) {
    try {
      return restTemplate.getForObject(
          "http://localhost:8080/api/users/user/" + userId,
          SubscriptionMessage.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to fetch user details", e);
    }
  }

  @Override
  public final User fetchAuthentication(final String jwtToken) {
    try {
      final HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + jwtToken);

      final HttpEntity<String> entity = new HttpEntity<>(headers);

      final ResponseEntity<User> user = restTemplate.exchange(
          "http://localhost:8080/api/users/auth",
          HttpMethod.GET,
          entity,
          new ParameterizedTypeReference<User>() {
          }
      );

      return user.getBody();
    } catch (HttpClientErrorException.Unauthorized e) {
      throw new RuntimeException("User is not authenticated: Unauthorized", e);
    } catch (RestClientException e) {
      throw new RuntimeException("Failed to fetch user authentication", e);
    }
  }
}
