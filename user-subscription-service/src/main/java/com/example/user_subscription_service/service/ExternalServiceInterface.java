package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.SubscriptionDto;
import com.example.user_subscription_service.dto.SubscriptionMessageDto;
import com.example.user_subscription_service.dto.UserDto;

public interface ExternalServiceInterface {

  SubscriptionDto fetchSubscription(Long subscriptionId);

  SubscriptionMessageDto fetchSubscriptionMessage(Long userId);

  UserDto fetchAuthentication();
}
