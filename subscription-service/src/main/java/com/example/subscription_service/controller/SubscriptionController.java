package com.example.subscription_service.controller;

import com.example.subscription_service.context.JwtContextHolder;
import com.example.subscription_service.model.Subscription;
import com.example.subscription_service.service.AddSubscriptionServiceInterface;
import com.example.subscription_service.service.GetSubscriptionsServiceInterface;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController implements SubscriptionControllerInterface {

  private final AddSubscriptionServiceInterface addSubscriptionService;
  private final GetSubscriptionsServiceInterface getSubscriptionsService;
  @Value("${admin.email}")
  private String adminEmail;

  /**
   * Adds a new subscription to the database.
   *
   * @param subscription        The subscription to be added.
   * @param authorizationHeader The authorization header containing the JWT token.
   * @return The added subscription.
   */
  @Override
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public final Subscription addSubscription(@Valid @RequestBody final Subscription subscription,
                                            @RequestHeader(
                                                "Authorization") final String authorizationHeader) {
    JwtContextHolder.setJwtToken(authorizationHeader.substring(7));
    try {
      return addSubscriptionService.addSubscription(subscription);
    } finally {
      JwtContextHolder.clear();
    }
  }

  /**
   * Retrieves all subscriptions from the database.
   *
   * @param sortBy The parameter to sort the results by. Defaults to "id".
   * @param order  The order to sort the results in. Defaults to "asc".
   * @return A list of all subscriptions from the database.
   */
  @Override
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public final List<Subscription> getAllSubscriptions(
      @RequestParam(defaultValue = "id") final String sortBy,
      @RequestParam(defaultValue = "asc") final String order) {
    Sort.Direction direction = order.equalsIgnoreCase(
        "desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(direction, sortBy);
    return getSubscriptionsService.getAllSubscriptions(sort);
  }

  /**
   * Retrieves a subscription by its ID from the database.
   *
   * @param subscriptionId The ID of the subscription to be retrieved.
   * @return The subscription with the given ID.
   */
  @Override
  @GetMapping("/{subscriptionId}")
  @ResponseStatus(HttpStatus.OK)
  public final Subscription getSubscriptionById(@Valid @PathVariable final Long subscriptionId) {
    return getSubscriptionsService.getSubscriptionById(subscriptionId);
  }

  /**
   * Retrieves a list of subscriptions from the database using a variety of filters.
   *
   * @param name     The name to search for. If provided, will only return subscriptions
   *                 with a matching name.
   * @param minPrice The minimum price to filter by. If provided, will only return
   *                 subscriptions with a price greater than or equal to this value.
   * @param maxPrice The maximum price to filter by. If provided, will only return
   *                 subscriptions with a price less than or equal to this value.
   * @return A list of subscriptions from the database.
   */
  @Override
  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public final List<Subscription> searchSubscriptions(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice) {

    if (Objects.nonNull(name)) {
      return getSubscriptionsService.searchByName(name);
    } else if (Objects.nonNull(minPrice) && Objects.nonNull(maxPrice)) {
      return getSubscriptionsService.filterByPriceRange(minPrice, maxPrice);
    }
    return getSubscriptionsService.getAllSubscriptions(Sort.by(Sort.Direction.ASC, "id"));
  }

}
