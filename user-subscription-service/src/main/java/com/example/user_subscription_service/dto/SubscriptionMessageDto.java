package com.example.user_subscription_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SubscriptionMessageDto {

  @NotNull(message = "ID cannot be null")
  private Long id;

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email cannot be blank")
  @Size(max = 255, message = "Email cannot exceed 255 characters")
  private String email;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @NotNull(message = "CreatedAt timestamp cannot be null")
  @PastOrPresent(message = "Creation date cannot be in the future")
  private LocalDateTime createdAt;

  @NotNull(message = "UpdatedAt timestamp cannot be null")
  @PastOrPresent(message = "Updated date cannot be in the future")
  private LocalDateTime updatedAt;
}
