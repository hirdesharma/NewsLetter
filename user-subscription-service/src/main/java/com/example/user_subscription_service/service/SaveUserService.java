package com.example.user_subscription_service.service;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.model.UserSubscription;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SaveUserService implements SaveUserServiceInterface {

  private final ExternalService externalService;
  private final SubscriptionMessageProcessorInterface subscriptionMessageProcessor;

  @Override
  public final UserSubscription saveUserSubscriptionToDB(final UserSubscription userSubscription) {
    final Subscription subscription = externalService.fetchSubscription(
        userSubscription.getSubscriptionId());
    checkNull(subscription, userSubscription);
    return subscriptionMessageProcessor.publishKafkaMessage(subscription, userSubscription);
  }

  private void checkNull(final Subscription subscription, final UserSubscription userSubscription) {
    if (Objects.isNull(subscription)) {
      System.out.println(
          "Subscription details not found for ID: " + userSubscription.getSubscriptionId());
      throw new RuntimeException(
          "Subscription details not found for ID: " + userSubscription.getSubscriptionId());
    }
  }
}
