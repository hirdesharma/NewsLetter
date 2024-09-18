package com.example.notification.exception;

public class NotificationServiceException extends RuntimeException {
  public NotificationServiceException(String message) {
    super(message);
  }

  public NotificationServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
