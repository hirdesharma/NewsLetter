package com.example.user_subscription_service.service;

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

  @Override
  public final List<UserSubscription> getUserSubscriptions(final Long userId) {
    return userSubscriptionRepository.findByUserId(userId);
  }

  @Override
  public final UserSubscription subscribe(final UserSubscription userSubscription) {
    final UserSubscription savedSubscription =
        saveUserService.saveUserSubscriptionToDB(userSubscription);

    final String cacheKey = "userSubscriptions:" + userSubscription.getUserId();
    final List<UserSubscription> subscriptions = getUserSubscriptions(userSubscription.getUserId());

    redisService.set(cacheKey, subscriptions, 3600L);

    return savedSubscription;
  }
}