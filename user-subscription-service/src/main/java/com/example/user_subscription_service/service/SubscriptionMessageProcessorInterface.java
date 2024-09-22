package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.SubscriptionDto;
import com.example.user_subscription_service.model.UserSubscription;

public interface SubscriptionMessageProcessorInterface {
  UserSubscription publishKafkaMessage(SubscriptionDto subscription,
                                       UserSubscription userSubscription);
}
