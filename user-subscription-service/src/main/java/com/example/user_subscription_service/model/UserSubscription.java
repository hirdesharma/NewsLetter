package com.example.user_subscription_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "user_subscriptions")
public class UserSubscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "duration")
  @Min(value = 1, message = "Duration must be at least 1 day")
  private Long duration;

  @Column(name = "startDate", nullable = false, updatable = false)
  @PastOrPresent(message = "Creation date cannot be in the future")
  private LocalDate startDate;

  @Column(name = "endDate", nullable = false)
  private LocalDate endDate;

  @Column(name = "updatedAt", nullable = false)
  @PastOrPresent(message = "Update date cannot be in the future")
  private LocalDate updatedAt = LocalDate.now();

  @Column(name = "isActive")
  @NotNull(message = "Active status cannot be null")
  private Boolean isActive;

  @Column(name = "subscriptionId", nullable = false)
  @NotNull(message = "Subscription ID cannot be null")
  @Min(value = 1, message = "Subscription ID must be a positive number")
  private Long subscriptionId;

  @Column(name = "userId", nullable = false)
  @NotNull(message = "User ID cannot be null")
  @Min(value = 1, message = "User ID must be a positive number")
  private Long userId;
}
