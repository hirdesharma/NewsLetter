package com.example.user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  @Email(message = "Email should be valid")
  @NotBlank(message = "Email cannot be blank")
  private String email;

  @Column(name = "password", nullable = false)
  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @Column(name = "createdAt", nullable = false, updatable = false)
  @PastOrPresent(message = "Creation date cannot be in the future")
  private LocalDateTime createdAt;

  @Column(name = "updatedAt", nullable = false)
  @PastOrPresent(message = "Creation date cannot be in the future")
  private LocalDateTime updatedAt;

  @Column(name = "role")
  private String role = "ROLE_USER";
  @Column(name = "jwt_token")
  private String jwtToken;
}
