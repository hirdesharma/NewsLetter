package com.example.user_subscription_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.Data;

@Data
public class SubscriptionDto {

  @NotNull(message = "ID cannot be null")
  private Long id;

  @NotNull(message = "Duration cannot be null")
  @Min(value = 1, message = "Duration must be at least 1 month")
  private Long duration;

  @NotBlank(message = "Name cannot be blank")
  private String name;

  @NotNull(message = "Renewable status cannot be null")
  private Boolean isRenewable;

  @NotNull(message = "Price cannot be null")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
  private Double price;

  @NotNull(message = "Start date cannot be null")
  @PastOrPresent(message = "Start date cannot be in the future")
  private LocalDate startDate;

  @NotNull(message = "Updated at date cannot be null")
  @PastOrPresent(message = "Updated at date cannot be in the future")
  private LocalDate updatedAt;
}
