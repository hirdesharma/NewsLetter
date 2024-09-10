package com.example.subscription_service.controller;

import com.example.subscription_service.model.Subscription;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface SubscriptionControllerInterface {
//  Subscription addSubscription(@RequestBody Subscription subscription);

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  Subscription addSubscription(@Valid @RequestBody Subscription subscription,
                               @RequestHeader(
                                   "Authorization") String authorizationHeader);

  List<Subscription> getAllSubscriptions(@RequestParam(defaultValue = "id") String sortBy,
                                         @RequestParam(defaultValue = "asc") String order);

  @GetMapping("/{subscriptionId}")
  @ResponseStatus(HttpStatus.OK)
  Subscription getSubscriptionById(@Valid @PathVariable Long subscriptionId);
}
