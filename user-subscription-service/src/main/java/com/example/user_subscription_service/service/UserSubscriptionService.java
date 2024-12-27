package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.UserDto;
import com.example.user_subscription_service.model.UserSubscription;
import com.example.user_subscription_service.repository.UserSubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService implements UserSubscriptionServiceInterface {

  private final RedisService redisService;
  private final UserSubscriptionRepository userSubscriptionRepository;
  private final SaveUserService saveUserService;
  private final ExternalService externalService;

  @Override
  public final List<UserSubscription> getUserSubscriptions(final Long userId) {
    final UserDto user = externalService.fetchAuthentication();
    if (!user.getId().equals(userId)) {
      System.out.println("you are not allowed to add subscription to any other user");
      throw new IllegalArgumentException(
          "you are not allowed to add subscription to any other user");
    }
    return userSubscriptionRepository.findByUserId(userId);
  }

  @Override
  public final UserSubscription subscribe(final UserSubscription userSubscription) {
    final UserDto user = externalService.fetchAuthentication();
    if (!user.getId().equals(userSubscription.getUserId())) {
      System.out.println("you are not allowed to add subscription to any other user");
      throw new IllegalArgumentException(
          "you are not allowed to add subscription to any other user");
    }

    final UserSubscription savedSubscription =
        saveUserService.saveUserSubscriptionToDB(userSubscription);

    final String cacheKey = "userSubscriptions:" + userSubscription.getUserId();
    final List<UserSubscription> subscriptions =
        getUserSubscriptions(userSubscription.getUserId());

    redisService.set(cacheKey, subscriptions, 3600L);

    return savedSubscription;
  }
}