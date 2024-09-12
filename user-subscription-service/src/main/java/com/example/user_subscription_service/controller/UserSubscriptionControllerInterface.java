package com.example.user_subscription_service.controller;

import com.example.user_subscription_service.model.UserSubscription;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface UserSubscriptionControllerInterface {
  UserSubscription subscribe(@RequestBody UserSubscription userSubscription, @RequestHeader(
      "Authorization") final String authorizationHeader);

  List<UserSubscription> getUserSubscriptions(@PathVariable Long userId, @RequestHeader(
      "Authorization") final String authorizationHeader);
}
