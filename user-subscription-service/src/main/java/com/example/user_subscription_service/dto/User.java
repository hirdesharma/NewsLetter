package com.example.user_subscription_service.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class User {

  @Id
  private Long id;

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email cannot be blank")
  private String email;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @PastOrPresent(message = "Creation date cannot be in the future")
  private LocalDateTime createdAt;

  @PastOrPresent(message = "Creation date cannot be in the future")
  private LocalDateTime updatedAt;

  private String role;

  private String jwtToken;
}
