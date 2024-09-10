package com.example.user_subscription_service.service;

import com.example.user_subscription_service.model.UserSubscription;

public interface SaveUserServiceInterface {
  UserSubscription saveUserSubscriptionToDB(UserSubscription userSubscription);
}
