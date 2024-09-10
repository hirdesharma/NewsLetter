package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.subscription_service.model.Subscription;
import com.example.subscription_service.repository.SubscriptionRepository;
import com.example.subscription_service.service.AddSubscriptionService;
import com.example.subscription_service.service.ExternalService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AddSubscriptionServiceTest {

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @Mock
  private ExternalService externalService;

  @InjectMocks
  private AddSubscriptionService addSubscriptionService;

  private final String mockedJwtToken = "mockedJwtToken";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAddSubscriptionSuccess() {
    Subscription subscription = new Subscription();
    subscription.setId(1L);

    // Mocking the external service to return true (user is authenticated)
    when(externalService.fetchSubscription(mockedJwtToken)).thenReturn(true);

    when(subscriptionRepository.findById(subscription.getId())).thenReturn(Optional.empty());
    when(subscriptionRepository.save(subscription)).thenReturn(subscription);

    Subscription result = addSubscriptionService.addSubscription(subscription, mockedJwtToken);

    assertNotNull(result);
    assertEquals(subscription, result);
    verify(subscriptionRepository).save(subscription);
  }

  @Test
  void testAddSubscriptionNullSubscription() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      addSubscriptionService.addSubscription(null, mockedJwtToken);
    });

    assertEquals("Subscription cannot be null", thrown.getMessage());
  }

  @Test
  void testAddSubscriptionAlreadyExists() {
    Subscription subscription = new Subscription();
    subscription.setId(1L);

    // Mocking the external service to return true (user is authenticated)
    when(externalService.fetchSubscription(mockedJwtToken)).thenReturn(true);

    when(subscriptionRepository.findById(subscription.getId())).thenReturn(
        Optional.of(subscription));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      addSubscriptionService.addSubscription(subscription, mockedJwtToken);
    });

    assertEquals("Subscription with ID " + subscription.getId() + " already exists",
        thrown.getMessage());
  }

  @Test
  void testAddSubscriptionNotAuthenticated() {
    Subscription subscription = new Subscription();
    subscription.setId(1L);

    // Mocking the external service to return false (user is not authenticated)
    when(externalService.fetchSubscription(mockedJwtToken)).thenReturn(false);

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      addSubscriptionService.addSubscription(subscription, mockedJwtToken);
    });

    assertEquals("you are not authenticated to do this", thrown.getMessage());
  }
}
