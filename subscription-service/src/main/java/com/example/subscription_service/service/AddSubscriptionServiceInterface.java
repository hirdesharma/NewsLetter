package com.example.subscription_service.service;

import com.example.subscription_service.model.Subscription;

public interface AddSubscriptionServiceInterface {
  Subscription addSubscription(Subscription subscription,String jwtToken);
}
