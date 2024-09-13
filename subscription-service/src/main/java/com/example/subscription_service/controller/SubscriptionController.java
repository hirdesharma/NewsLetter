package com.example.subscription_service.controller;

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

  @Override
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public final Subscription addSubscription(@Valid @RequestBody final Subscription subscription,
                                            @RequestHeader(
                                                "Authorization") final String authorizationHeader) {
    final String jwtToken = authorizationHeader.substring(7);
    return addSubscriptionService.addSubscription(subscription, jwtToken);
  }

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

  @Override
  @GetMapping("/{subscriptionId}")
  @ResponseStatus(HttpStatus.OK)
  public final Subscription getSubscriptionById(@Valid @PathVariable final Long subscriptionId) {
    return getSubscriptionsService.getSubscriptionById(subscriptionId);
  }

  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public final List<Subscription> searchSubscriptions(
      @RequestParam(required = false) final String name,
      @RequestParam(required = false) final Long id) {

    if (Objects.isNull(name) && Objects.isNull(id)) {
      throw new IllegalArgumentException("At least one of 'name' or 'id' must be provided.");
    }

    return getSubscriptionsService.searchSubscriptions(name, id);
  }
}
