package com.example.subscription_service.service;

import com.example.subscription_service.model.Subscription;
import com.example.subscription_service.repository.SubscriptionRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddSubscriptionService implements AddSubscriptionServiceInterface {

  private final SubscriptionRepository subscriptionRepository;
  private final ExternalService externalService;

  @Override
  public final Subscription addSubscription(final Subscription subscription,
                                            final String jwtToken) {
    if (Objects.isNull(subscription)) {
      throw new IllegalArgumentException("Subscription cannot be null");
    }
    if (!externalService.fetchSubscription(jwtToken).getRole().equals("ROLE_ADMIN")) {
      throw new RuntimeException("you are not authenticated to add new subscription");
    }
    // Check if subscription with the given ID already exists
    Optional<Subscription> existingSubscription = subscriptionRepository.findById(
        subscription.getId());

    if (existingSubscription.isPresent()) {
      throw new RuntimeException(
          "Subscription with ID " + subscription.getId() + " already exists");
    }
    return subscriptionRepository.save(subscription);
  }

}
