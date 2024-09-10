package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.dto.SubscriptionMessage;

public interface ExternalServiceInterface {

  Subscription fetchSubscription(Long subscriptionId);

  SubscriptionMessage fetchSubscriptionMessage(Long userId);
}
