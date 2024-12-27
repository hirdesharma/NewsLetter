package com.example.subscription_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "subscriptions")
public class Subscription {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "duration", nullable = false)
  @Min(value = 1, message = "Duration must be at least 1 day")
  private Long duration; // in days

  @Column(name = "name", nullable = false)
  @NotBlank(message = "Name cannot be blank")
  private String name;

  @Column(name = "isRenewable", nullable = false)
  @NotNull(message = "Renewable status cannot be null")
  private Boolean isRenewable;

  @Column(name = "price", nullable = false)
  @Positive(message = "Price must be positive")
  private Double price;

  @Column(name = "startDate", nullable = false)
  @NotNull(message = "Start date cannot be null")
  private LocalDate startDate = LocalDate.now();

  @Column(name = "updatedAt", nullable = false)
  @NotNull(message = "Updated date cannot be null")
  private LocalDate updatedAt = LocalDate.now();
}
