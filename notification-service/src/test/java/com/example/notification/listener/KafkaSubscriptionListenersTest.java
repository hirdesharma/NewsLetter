package com.example.notification.listener;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.never;

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
  void testListener_Success() throws Exception {
    String data = "{\"id\":\"12345\",\"email\":\"test@example.com\"}";
    JsonNode jsonNode = mock(JsonNode.class);
    when(objectMapper.readTree(data)).thenReturn(jsonNode);
    when(jsonNode.get("id")).thenReturn(mock(JsonNode.class));
    when(jsonNode.get("id").asText()).thenReturn("12345");
    when(jsonNode.get("email")).thenReturn(mock(JsonNode.class));
    when(jsonNode.get("email").asText()).thenReturn("test@example.com");

    kafkaSubscriptionListeners.listener(data);

    String expectedBody
        = "Congratulations you { userId : 12345} have been subscribed to our subscription service";
    verify(emailSenderService).sendEmail("test@example.com", expectedBody);
  }
  // TestEdge cases and failing cases
}
