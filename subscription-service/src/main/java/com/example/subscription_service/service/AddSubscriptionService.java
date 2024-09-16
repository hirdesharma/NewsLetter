package com.example.subscription_service.service;

import com.example.subscription_service.dto.User;
import com.example.subscription_service.model.Subscription;
import com.example.subscription_service.repository.SubscriptionRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddSubscriptionService implements AddSubscriptionServiceInterface {

  private final SubscriptionRepository subscriptionRepository;
  private final ExternalService externalService;

  /**
   * Adds a new subscription to the database.
   *
   * @param subscription The subscription to be added.
   * @return The added subscription.
   * @throws IllegalArgumentException If the subscription is null.
   * @throws RuntimeException If the subscription already exists in the database or if the user is not
   *     authorized to add new subscriptions.
   */
  @Override
  public Subscription addSubscription(final Subscription subscription) {
    if (Objects.isNull(subscription)) {
      throw new IllegalArgumentException("Subscription cannot be null");
    }

    // Check if the user is authorized to add new subscriptions
    User user = externalService.fetchUserDetails();
    if (!"ROLE_ADMIN".equals(user.getRole())) {
      throw new RuntimeException("You are not authorized to add new subscriptions");
    }

    // Check if subscription with the given ID already exists
    if (subscriptionRepository.existsById(subscription.getId())) {
      throw new RuntimeException("Subscription with ID " + subscription.getId() + " already exists");
    }

    return subscriptionRepository.save(subscription);
  }
}
