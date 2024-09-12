package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.dto.SubscriptionMessage;
import com.example.user_subscription_service.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ExternalServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private ExternalService externalService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFetchSubscriptionSuccess() {
    Long subscriptionId = 1L;
    Subscription expectedSubscription = new Subscription();
    when(restTemplate.getForObject(anyString(), eq(Subscription.class)))
        .thenReturn(expectedSubscription);

    Subscription actualSubscription = externalService.fetchSubscription(subscriptionId);
    assertEquals(expectedSubscription, actualSubscription);
  }

  @Test
  void testFetchSubscriptionException() {
    Long subscriptionId = 1L;
    when(restTemplate.getForObject(anyString(), eq(Subscription.class)))
        .thenThrow(new RestClientException("Error"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        externalService.fetchSubscription(subscriptionId));
    assertEquals("Failed to fetch subscription details", thrown.getMessage());
  }

  @Test
  void testFetchSubscriptionMessageSuccess() {
    Long userId = 1L;
    SubscriptionMessage expectedMessage = new SubscriptionMessage();
    when(restTemplate.getForObject(anyString(), eq(SubscriptionMessage.class)))
        .thenReturn(expectedMessage);

    SubscriptionMessage actualMessage = externalService.fetchSubscriptionMessage(userId);
    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  void testFetchSubscriptionMessageException() {
    Long userId = 1L;
    when(restTemplate.getForObject(anyString(), eq(SubscriptionMessage.class)))
        .thenThrow(new RuntimeException("Error"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        externalService.fetchSubscriptionMessage(userId));
    assertEquals("Failed to fetch user details", thrown.getMessage());
  }

  @Test
  void testFetchAuthenticationUnauthorized() {
    String jwtToken = "invalidToken";
    when(restTemplate.exchange(
        anyString(),
        any(HttpMethod.class),
        any(HttpEntity.class),
        eq(User.class))
    ).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

    assertThrows(RuntimeException.class, () ->
        externalService.fetchAuthentication(jwtToken));
  }

  @Test
  void testFetchAuthenticationException() {
    String jwtToken = "anyToken";
    when(restTemplate.exchange(
        anyString(),
        any(HttpMethod.class),
        any(HttpEntity.class),
        eq(User.class))
    ).thenThrow(new RestClientException("Error"));

    assertThrows(RuntimeException.class, () ->
        externalService.fetchAuthentication(jwtToken));
  }
}
