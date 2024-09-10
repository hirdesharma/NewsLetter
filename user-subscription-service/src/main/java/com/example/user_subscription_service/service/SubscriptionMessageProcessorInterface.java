package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.model.UserSubscription;

public interface SubscriptionMessageProcessorInterface {
  UserSubscription publishKafkaMessage(Subscription subscription,
                                       UserSubscription userSubscription);
}
