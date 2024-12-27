package com.example.user_service.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserDto {
  private Long id;
  private String email;
  private String role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
