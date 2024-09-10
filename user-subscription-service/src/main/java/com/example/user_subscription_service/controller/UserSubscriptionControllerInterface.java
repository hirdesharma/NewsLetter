package com.example.user_subscription_service.controller;

import com.example.user_subscription_service.model.UserSubscription;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserSubscriptionControllerInterface {
  UserSubscription subscribe(@RequestBody UserSubscription userSubscription);

  List<UserSubscription> getUserSubscriptions(@PathVariable Long userId);
}
