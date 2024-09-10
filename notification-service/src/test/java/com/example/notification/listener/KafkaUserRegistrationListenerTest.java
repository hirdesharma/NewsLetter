package com.example.notification.listener;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

class KafkaUserRegistrationListenerTest {

  @Mock
  private EmailSenderService emailSenderService;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private KafkaUserRegistrationListener kafkaUserRegistrationListener;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testListenerSuccess() throws Exception {
    String data = "{\"id\":\"12345\",\"email\":\"test@example.com\"}";

    JsonNode jsonNode = mock(JsonNode.class);
    when(objectMapper.readTree(data)).thenReturn(jsonNode);
    when(jsonNode.get("id")).thenReturn(mock(JsonNode.class));
    when(jsonNode.get("id").asText()).thenReturn("12345");
    when(jsonNode.get("email")).thenReturn(mock(JsonNode.class));
    when(jsonNode.get("email").asText()).thenReturn("test@example.com");

    kafkaUserRegistrationListener.listener(data);

    // Verify that emailSenderService was called with the correct arguments
    verify(emailSenderService).sendEmail("test@example.com",
        "Congratulations you have successfully registered on out newsletter with email : "
            + "test@example.com");
  }


  @Test
  public void testNullOrEmptyMessageHandling() {
    EmailSenderService emailSenderService = mock(EmailSenderService.class);
    KafkaUserRegistrationListener listener = new KafkaUserRegistrationListener(emailSenderService);

    assertThrows(NotificationServiceException.class, () -> listener.listener(null));
    assertThrows(NotificationServiceException.class, () -> listener.listener(""));
  }

  @Test
  public void testMessageWithMissingFields() {
    EmailSenderService emailSenderService = mock(EmailSenderService.class);
    KafkaUserRegistrationListener listener = new KafkaUserRegistrationListener(emailSenderService);
    String invalidMessage = "{\"userId\":\"12345\"}";

    assertThrows(NotificationServiceException.class, () -> {
      listener.listener(invalidMessage);
    });

    verify(emailSenderService, never()).sendEmail(anyString(), anyString());
  }
}
