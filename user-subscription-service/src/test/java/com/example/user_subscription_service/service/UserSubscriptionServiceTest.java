package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.user_subscription_service.model.UserSubscription;
import com.example.user_subscription_service.repository.UserSubscriptionRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserSubscriptionServiceTest {

  @Mock
  private RedisService redisService;

  @Mock
  private UserSubscriptionRepository userSubscriptionRepository;

  @Mock
  private SaveUserService saveUserService;

  @InjectMocks
  private UserSubscriptionService userSubscriptionService;

  private UserSubscription userSubscription;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userSubscription = new UserSubscription();
    userSubscription.setUserId(1L);
    userSubscription.setSubscriptionId(100L);
    userSubscription.setIsActive(true);
  }

  @Test
  void testGetUserSubscriptions() {

    List<UserSubscription> expectedSubscriptions = new ArrayList<>();
    expectedSubscriptions.add(userSubscription);

    when(userSubscriptionRepository.findByUserId(userSubscription.getUserId()))
        .thenReturn(expectedSubscriptions);


    List<UserSubscription> result = userSubscriptionService.getUserSubscriptions(
        userSubscription.getUserId());


    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedSubscriptions, result);

    verify(userSubscriptionRepository, times(1)).findByUserId(userSubscription.getUserId());
  }

  @Test
  void testSubscribeSuccess() {

    UserSubscription savedSubscription = new UserSubscription();
    savedSubscription.setUserId(101L);
    savedSubscription.setSubscriptionId(234L);
    savedSubscription.setIsActive(true);

    List<UserSubscription> subscriptions = new ArrayList<>();
    subscriptions.add(savedSubscription);

    when(saveUserService.saveUserSubscriptionToDB(any(UserSubscription.class)))
        .thenReturn(savedSubscription);
    when(userSubscriptionRepository.findByUserId(userSubscription.getUserId()))
        .thenReturn(subscriptions);


    UserSubscription result = userSubscriptionService.subscribe(userSubscription);


    assertNotNull(result);
    assertEquals(savedSubscription, result);

    verify(saveUserService, times(1)).saveUserSubscriptionToDB(userSubscription);
    verify(userSubscriptionRepository, times(1)).findByUserId(userSubscription.getUserId());
    verify(redisService, times(1)).set(eq("userSubscriptions:" + userSubscription.getUserId()),
        eq(subscriptions), eq(3600L));
  }
}
