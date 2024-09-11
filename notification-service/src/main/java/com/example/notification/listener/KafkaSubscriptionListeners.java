package com.example.notification.listener;

import com.example.notification.exception.NotificationServiceException;
import com.example.notification.service.EmailSenderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSubscriptionListeners {

  private final EmailSenderService emailSenderService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @KafkaListener(topics = "${spring.kafka.topic.subscription-events}",
                 groupId = "${spring.kafka.group.subscription-notification}")
  void listener(final String data) {
    try {
      final JsonNode jsonNode = objectMapper.readTree(data);
      final String userId = jsonNode.get("id").asText();
      final String email = jsonNode.get("email").asText();

      System.out.println("Processed subscription for user: " + userId);
      final String body = "Congratulations you { userId : " + userId + "} have been subscribed to "
          + "our "
          + "subscription service";
      emailSenderService.sendEmail(email, body);
    } catch (Exception e) {
      throw new NotificationServiceException(
          "Error processing subscription message: " + e.getMessage(), e);
    }
  }
}
