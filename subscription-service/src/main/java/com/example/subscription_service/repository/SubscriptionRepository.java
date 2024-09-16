package com.example.subscription_service.repository;

import com.example.subscription_service.model.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
  /**
   * Finds a subscription by its ID.
   *
   * @param id the ID of the subscription to find.
   * @return an Optional containing the subscription if found, or an empty Optional otherwise.
   */
  Optional<Subscription> findByid(Long id);

  /**
   * Finds all subscriptions whose name contains the given keyword, ignoring case.
   *
   * @param name the keyword to search for.
   * @return a list of subscriptions whose name contains the given keyword.
   */
  List<Subscription> findByNameContainingIgnoreCase(String name);

  /**
   * Finds all subscriptions whose price is between the given min and max prices.
   *
   * @param minPrice the minimum price to filter by.
   * @param maxPrice the maximum price to filter by.
   * @return a list of subscriptions whose price is between the given min and max prices.
   */
  List<Subscription> findByPriceBetween(Double minPrice, Double maxPrice);
}
