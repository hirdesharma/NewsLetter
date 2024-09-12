package com.example.user_subscription_service.service;

import com.example.user_subscription_service.model.UserSubscription;
import java.util.List;

public interface UserSubscriptionServiceInterface {
  UserSubscription subscribe(final UserSubscription userSubscription,String jwtToken);

  List<UserSubscription> getUserSubscriptions(Long userId,String jwtToken);
}
