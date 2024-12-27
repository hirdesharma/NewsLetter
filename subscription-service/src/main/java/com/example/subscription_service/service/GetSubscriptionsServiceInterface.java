package com.example.subscription_service.service;

import com.example.subscription_service.model.Subscription;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface GetSubscriptionsServiceInterface {

  List<Subscription> getAllSubscriptions(Sort sort);

  Subscription getSubscriptionById(Long subscriptionId);

  List<Subscription> searchByName(String name);

  List<Subscription> filterByPriceRange(Double minPrice, Double maxPrice);
}
