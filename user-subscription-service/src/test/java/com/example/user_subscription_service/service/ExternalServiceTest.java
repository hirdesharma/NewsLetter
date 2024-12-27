package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import com.example.user_subscription_service.dto.SubscriptionDto;
import com.example.user_subscription_service.dto.SubscriptionMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
    SubscriptionDto expectedSubscription = new SubscriptionDto();
    when(restTemplate.getForObject(anyString(), eq(SubscriptionDto.class)))
        .thenReturn(expectedSubscription);

    SubscriptionDto actualSubscription = externalService.fetchSubscription(subscriptionId);
    assertEquals(expectedSubscription, actualSubscription);
  }

  @Test
  void testFetchSubscriptionException() {
    Long subscriptionId = 1L;
    when(restTemplate.getForObject(anyString(), eq(SubscriptionDto.class)))
        .thenThrow(new RestClientException("Error"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        externalService.fetchSubscription(subscriptionId));
    assertEquals("Failed to fetch subscription details", thrown.getMessage());
  }

  @Test
  void testFetchSubscriptionMessageSuccess() {
    Long userId = 1L;
    SubscriptionMessageDto expectedMessage = new SubscriptionMessageDto();
    when(restTemplate.getForObject(anyString(), eq(SubscriptionMessageDto.class)))
        .thenReturn(expectedMessage);

    SubscriptionMessageDto actualMessage = externalService.fetchSubscriptionMessage(userId);
    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  void testFetchSubscriptionMessageException() {
    Long userId = 1L;
    when(restTemplate.getForObject(anyString(), eq(SubscriptionMessageDto.class)))
        .thenThrow(new RuntimeException("Error"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () ->
        externalService.fetchSubscriptionMessage(userId));
    assertEquals("Failed to fetch user details", thrown.getMessage());
  }

}
