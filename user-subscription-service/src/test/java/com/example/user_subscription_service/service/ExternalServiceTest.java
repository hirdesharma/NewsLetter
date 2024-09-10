package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.dto.SubscriptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class ExternalServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private ExternalService externalService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFetchSubscription_Success() {
    Subscription expectedSubscription = new Subscription();
    expectedSubscription.setId(1L);
    expectedSubscription.setName("Basic Plan");

    when(restTemplate.getForObject(
        "http://localhost:8081/api/subscriptions/1",
        Subscription.class))
        .thenReturn(expectedSubscription);

    Subscription actualSubscription = externalService.fetchSubscription(1L);

    assertNotNull(actualSubscription);
    assertEquals(expectedSubscription.getId(), actualSubscription.getId());
    assertEquals(expectedSubscription.getName(), actualSubscription.getName());
  }

  @Test
  void testFetchSubscription_Failure() {
    when(restTemplate.getForObject(
        "http://localhost:8081/api/subscriptions/1",
        Subscription.class))
        .thenThrow(new RestClientException("Failed to fetch subscription"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      externalService.fetchSubscription(1L);
    });

    assertEquals("Failed to fetch subscription details", thrown.getMessage());
  }

  @Test
  void testFetchSubscriptionMessage_Success() {
    SubscriptionMessage expectedMessage = new SubscriptionMessage();
    expectedMessage.setId(1L);

    when(restTemplate.getForObject(
        "http://localhost:8080/api/users/1",
        SubscriptionMessage.class))
        .thenReturn(expectedMessage);

    SubscriptionMessage actualMessage = externalService.fetchSubscriptionMessage(1L);

    assertNotNull(actualMessage);
    assertEquals(expectedMessage.getId(), actualMessage.getId());
  }

  @Test
  void testFetchSubscriptionMessage_Failure() {
    when(restTemplate.getForObject(
        "http://localhost:8080/api/users/1",
        SubscriptionMessage.class))
        .thenThrow(new RestClientException("Failed to fetch user details"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      externalService.fetchSubscriptionMessage(1L);
    });

    assertEquals("Failed to fetch user details", thrown.getMessage());
  }
}
