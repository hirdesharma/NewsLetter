package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.subscription_service.dto.User;
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

    // Mocking the external service to return a User with ROLE_ADMIN
    User mockUser = new User();
    mockUser.setRole("ROLE_ADMIN");
    when(externalService.fetchSubscription(mockedJwtToken)).thenReturn(mockUser);

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

    // Mocking the external service to return a User with ROLE_ADMIN
    User mockUser = new User();
    mockUser.setRole("ROLE_ADMIN");
    when(externalService.fetchSubscription(mockedJwtToken)).thenReturn(mockUser);

    when(subscriptionRepository.findById(subscription.getId())).thenReturn(
        Optional.of(subscription));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      addSubscriptionService.addSubscription(subscription, mockedJwtToken);
    });

    assertEquals("Subscription with ID " + subscription.getId() + " already exists",
        thrown.getMessage());
  }

  @Test
  void testAddSubscriptionNotAdmin() {
    Subscription subscription = new Subscription();
    subscription.setId(1L);

    // Mocking the external service to return a User with ROLE_USER
    User mockUser = new User();
    mockUser.setRole("ROLE_USER");
    when(externalService.fetchSubscription(mockedJwtToken)).thenReturn(mockUser);

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      addSubscriptionService.addSubscription(subscription, mockedJwtToken);
    });

    assertEquals("you are not authenticated to add new subscription", thrown.getMessage());
  }

  @Test
  void testAddSubscriptionNotAuthenticated() {
    Subscription subscription = new Subscription();
    subscription.setId(1L);

    // Mocking the external service to throw an exception (user not authenticated)
    when(externalService.fetchSubscription(mockedJwtToken))
        .thenThrow(new RuntimeException("User is not authenticated: Unauthorized"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      addSubscriptionService.addSubscription(subscription, mockedJwtToken);
    });

    assertEquals("User is not authenticated: Unauthorized", thrown.getMessage());
  }
}
