package com.example.subscription_service.service;

import com.example.subscription_service.model.Subscription;
import com.example.subscription_service.repository.SubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSubscriptionsService implements GetSubscriptionsServiceInterface {
  private final SubscriptionRepository subscriptionRepository;

  /**
   * Retrieves all subscriptions from the database.
   *
   * @param sort The sort options to apply to the results.
   * @return A list of all subscriptions from the database.
   */
  @Override
  public final List<Subscription> getAllSubscriptions(final Sort sort) {
    return subscriptionRepository.findAll(sort);
  }

  /**
   * Retrieves a subscription by its ID from the database.
   *
   * @param subscriptionId The ID of the subscription to be retrieved.
   * @return The subscription with the given ID.
   * @throws IllegalArgumentException If the subscription with the given ID does not exist
   */
  @Override
  public final Subscription getSubscriptionById(final Long subscriptionId) {
    return subscriptionRepository.findByid(subscriptionId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Subscription not found with id: " + subscriptionId));
  }

  /**
   * Retrieves a list of subscriptions with a name that matches the given name (case insensitive).
   *
   * @param name The name to search for.
   * @return A list of subscriptions with a name matching the given name.
   */
  @Override
  public List<Subscription> searchByName(String name) {
    return subscriptionRepository.findByNameContainingIgnoreCase(name);
  }

  /**
   * Retrieves a list of subscriptions from the database with a price greater than or equal to
   * the given minimum price and less than or equal to the given maximum price.
   *
   * @param minPrice The minimum price to filter by.
   * @param maxPrice The maximum price to filter by.
   * @return A list of subscriptions with a price within the given range.
   */
  @Override
  public List<Subscription> filterByPriceRange(Double minPrice, Double maxPrice) {
    return subscriptionRepository.findByPriceBetween(minPrice, maxPrice);
  }
}
