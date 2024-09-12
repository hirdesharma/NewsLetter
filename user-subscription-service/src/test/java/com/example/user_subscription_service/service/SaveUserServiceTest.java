package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.model.UserSubscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SaveUserServiceTest {

  @Mock
  private ExternalService externalService;

  @Mock
  private SubscriptionMessageProcessorInterface subscriptionMessageProcessor;

  @InjectMocks
  private SaveUserService saveUserService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSaveUserSubscriptionToDBSuccessful() {
    UserSubscription userSubscription = new UserSubscription();
    userSubscription.setSubscriptionId(1L);

    Subscription subscription = new Subscription();
    subscription.setId(1L);

    UserSubscription expectedUserSubscription = new UserSubscription();
    expectedUserSubscription.setId(100L);

    when(externalService.fetchSubscription(userSubscription.getSubscriptionId()))
        .thenReturn(subscription);
    when(subscriptionMessageProcessor.publishKafkaMessage(subscription, userSubscription))
        .thenReturn(expectedUserSubscription);

    UserSubscription result = saveUserService.saveUserSubscriptionToDB(userSubscription);

    assertNotNull(result);
    assertEquals(expectedUserSubscription, result);
    verify(externalService, times(1)).fetchSubscription(userSubscription.getSubscriptionId());
    verify(subscriptionMessageProcessor, times(1))
        .publishKafkaMessage(subscription, userSubscription);
  }

  @Test
  void testSaveUserSubscriptionToDBNullSubscription() {

    UserSubscription userSubscription = new UserSubscription();
    userSubscription.setSubscriptionId(1L);

    when(externalService.fetchSubscription(userSubscription.getSubscriptionId()))
        .thenReturn(null);


    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      saveUserService.saveUserSubscriptionToDB(userSubscription);
    });

    assertEquals("Subscription details not found for ID: 1", thrown.getMessage());
    verify(externalService, times(1)).fetchSubscription(userSubscription.getSubscriptionId());
    verify(subscriptionMessageProcessor, never())
        .publishKafkaMessage(any(Subscription.class), any(UserSubscription.class));
  }

  @Test
  void testSaveUserSubscriptionToDBExternalServiceThrowsException() {

    UserSubscription userSubscription = new UserSubscription();
    userSubscription.setSubscriptionId(1L);

    when(externalService.fetchSubscription(userSubscription.getSubscriptionId()))
        .thenThrow(new RuntimeException("Failed to fetch subscription"));


    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      saveUserService.saveUserSubscriptionToDB(userSubscription);
    });

    assertEquals("Failed to fetch subscription", thrown.getMessage());
    verify(externalService, times(1)).fetchSubscription(userSubscription.getSubscriptionId());
    verify(subscriptionMessageProcessor, never())
        .publishKafkaMessage(any(Subscription.class), any(UserSubscription.class));
  }
}
