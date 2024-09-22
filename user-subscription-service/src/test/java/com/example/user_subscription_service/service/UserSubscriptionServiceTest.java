package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.user_subscription_service.dto.UserDto;
import com.example.user_subscription_service.model.UserSubscription;
import com.example.user_subscription_service.repository.UserSubscriptionRepository;
import java.util.Collections;
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

  @Mock
  private ExternalService externalService;

  @InjectMocks
  private UserSubscriptionService userSubscriptionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getUserSubscriptions_ShouldReturnSubscriptions_WhenUserIsAuthenticated() {
    Long userId = 1L;

    UserDto userDto = new UserDto();
    userDto.setId(userId);

    List<UserSubscription> subscriptions = Collections.singletonList(new UserSubscription());

    when(externalService.fetchAuthentication()).thenReturn(userDto);
    when(userSubscriptionRepository.findByUserId(userId)).thenReturn(subscriptions);

    List<UserSubscription> result = userSubscriptionService.getUserSubscriptions(userId);

    assertEquals(subscriptions, result);
    verify(userSubscriptionRepository).findByUserId(userId);
  }

  @Test
  void getUserSubscriptions_ShouldThrowException_WhenUserIdDoesNotMatch() {
    Long userId = 1L;
    Long otherUserId = 2L;

    UserDto userDto = new UserDto();
    userDto.setId(otherUserId);

    when(externalService.fetchAuthentication()).thenReturn(userDto);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userSubscriptionService.getUserSubscriptions(userId);
    });

    assertEquals("you are not allowed to add subscription to any other user",
        exception.getMessage());
  }

  @Test
  void subscribe_ShouldSaveSubscriptionAndCacheIt_WhenUserIsAuthenticated() {
    Long userId = 1L;

    UserDto userDto = new UserDto();
    userDto.setId(userId);

    UserSubscription subscription = new UserSubscription();
    subscription.setUserId(userId);

    UserSubscription savedSubscription = new UserSubscription();

    when(externalService.fetchAuthentication()).thenReturn(userDto);
    when(saveUserService.saveUserSubscriptionToDB(subscription)).thenReturn(savedSubscription);
    when(userSubscriptionRepository.findByUserId(userId)).thenReturn(
        Collections.singletonList(subscription));

    UserSubscription result = userSubscriptionService.subscribe(subscription);

    assertEquals(savedSubscription, result);

    verify(saveUserService).saveUserSubscriptionToDB(subscription);
    verify(redisService).set(eq("userSubscriptions:" + userId), anyList(), eq(3600L));
  }

  @Test
  void subscribe_ShouldThrowException_WhenUserIdDoesNotMatch() {
    Long userId = 1L;
    Long otherUserId = 2L;

    UserDto userDto = new UserDto();
    userDto.setId(otherUserId);

    UserSubscription subscription = new UserSubscription();
    subscription.setUserId(userId);

    when(externalService.fetchAuthentication()).thenReturn(userDto);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userSubscriptionService.subscribe(subscription);
    });

    assertEquals("you are not allowed to add subscription to any other user",
        exception.getMessage());
  }

}
