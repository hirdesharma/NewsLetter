package com.example.subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.subscription_service.model.Subscription;
import com.example.subscription_service.repository.SubscriptionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

class GetSubscriptionsServiceTest {

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @InjectMocks
  private GetSubscriptionsService getSubscriptionsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAllSubscriptionsSortedByIdAsc() {
    Subscription subscription1 = new Subscription();
    subscription1.setId(1L);
    Subscription subscription2 = new Subscription();
    subscription2.setId(2L);

    when(subscriptionRepository.findAll(Sort.by(Sort.Direction.ASC, "id")))
        .thenReturn(List.of(subscription1, subscription2));

    List<Subscription> subscriptions = getSubscriptionsService.getAllSubscriptions(
        Sort.by(Sort.Direction.ASC, "id"));

    assertNotNull(subscriptions);
    assertEquals(2, subscriptions.size());
    assertEquals(1L, subscriptions.get(0).getId());
    assertEquals(2L, subscriptions.get(1).getId());
  }


  @Test
  void testGetSubscriptionByIdSuccess() {
    Long subscriptionId = 1L;
    Subscription subscription = new Subscription();
    when(subscriptionRepository.findByid(subscriptionId)).thenReturn(Optional.of(subscription));

    Subscription result = getSubscriptionsService.getSubscriptionById(subscriptionId);

    assertNotNull(result);
    assertSame(subscription, result);
  }

  @Test
  void testGetSubscriptionByIdNotFound() {
    Long subscriptionId = 1L;
    when(subscriptionRepository.findByid(subscriptionId)).thenReturn(Optional.empty());

    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      getSubscriptionsService.getSubscriptionById(subscriptionId);
    });

    assertEquals("Subscription not found with id: " + subscriptionId, thrown.getMessage());
  }
}