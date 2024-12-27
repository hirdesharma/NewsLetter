package com.example.notification.listener;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.notification.exception.NotificationServiceException;
import com.example.notification.service.EmailSenderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class KafkaSubscriptionListenersTest {

  @Mock
  private EmailSenderService emailSenderService;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private KafkaSubscriptionListeners kafkaSubscriptionListeners;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testListenerSuccess() throws Exception {
    String data = "{\"id\":\"12345\",\"email\":\"test@example.com\"}";
    JsonNode jsonNode = new ObjectMapper().readTree(data);
    when(objectMapper.readTree(data)).thenReturn(jsonNode);
    kafkaSubscriptionListeners.listener(data);

    verify(emailSenderService).sendEmailSubscription("test@example.com", "12345");
  }

  @Test
  void testListenerNullData() {
    assertThrows(NotificationServiceException.class,
        () -> kafkaSubscriptionListeners.listener(null));
  }

  @Test
  void testListenerEmptyData() {
    assertThrows(NotificationServiceException.class, () -> kafkaSubscriptionListeners.listener(""));
  }

  @Test
  void testListenerInvalidJson() {
    String data = "Invalid JSON";
    assertThrows(NotificationServiceException.class,
        () -> kafkaSubscriptionListeners.listener(data));
  }

  @Test
  void testListenerMissingIdField() {
    String data = "{\"email\":\"test@example.com\"}";
    assertThrows(NotificationServiceException.class,
        () -> kafkaSubscriptionListeners.listener(data));
  }
}
