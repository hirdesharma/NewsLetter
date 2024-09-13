package com.example.subscription_service.service;

import com.example.subscription_service.model.Subscription;
import com.example.subscription_service.repository.SubscriptionRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSubscriptionsService implements GetSubscriptionsServiceInterface {
  private final SubscriptionRepository subscriptionRepository;

  @Override
  public final List<Subscription> getAllSubscriptions(final Sort sort) {
    return subscriptionRepository.findAll(sort);
  }

  @Override
  public final Subscription getSubscriptionById(final Long subscriptionId) {
    return subscriptionRepository.findByid(subscriptionId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Subscription not found with id: " + subscriptionId));
  }

  @Override
  public List<Subscription> searchSubscriptions(String name, Long id) {
    if (Objects.nonNull(name) && Objects.nonNull(id)) {
      return subscriptionRepository.findByNameContainingIgnoreCaseOrId(name, id);
    } else if (Objects.nonNull(name)) {
      return subscriptionRepository.findByNameContainingIgnoreCase(name);
    } else {
      return subscriptionRepository.findById(id).map(List::of).orElse(List.of());
    }
  }
}
