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
    JsonNode jsonNode = new ObjectMapper().readTree(data);
    when(objectMapper.readTree(data)).thenReturn(jsonNode);
    kafkaUserRegistrationListener.listener(data);
    verify(emailSenderService).sendEmailRegistration("test@example.com");
  }

  @Test
  void testListenerHandlesNullOrEmptyMessage() {
    EmailSenderService emailSenderServiceMock = mock(EmailSenderService.class);
    KafkaUserRegistrationListener listener = new KafkaUserRegistrationListener(
        emailSenderServiceMock);

    assertThrows(NotificationServiceException.class, () -> listener.listener(null));
    assertThrows(NotificationServiceException.class, () -> listener.listener(""));
    verify(emailSenderServiceMock, never()).sendEmailRegistration(anyString());
  }

  @Test
  void testListenerHandlesMissingFields() {
    String messageWithoutEmail = "{\"id\":\"12345\"}";
    String messageWithoutId = "{\"email\":\"test@example.com\"}";

    assertThrows(NotificationServiceException.class,
        () -> kafkaUserRegistrationListener.listener(messageWithoutEmail));
    assertThrows(NotificationServiceException.class,
        () -> kafkaUserRegistrationListener.listener(messageWithoutId));

    verify(emailSenderService, never()).sendEmailRegistration(anyString());
  }
}
