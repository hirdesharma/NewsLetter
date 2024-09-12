package com.example.user_subscription_service.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Subscription {
  private Long id;
  private Long duration;
  private String name;
  private Boolean isRenewable;
  private Double price;
  private LocalDate startDate;
  private LocalDate updatedAt;
}
