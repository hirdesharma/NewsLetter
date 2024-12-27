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

  /**
   * This method is the entry point for the Kafka listener.
   * It consumes from the topic specified in the application properties.
   * It expects the message to contain a JSON payload with the following fields:
   * - id: the user id
   * - email: the user email
   * It extracts the fields from the JSON, logs a message indicating the user
   * has been subscribed and then sends an email to the user with the email
   * address extracted from the JSON.
   *
   * @param data the json payload from kafka
   */
  @KafkaListener(topics = "${spring.kafka.topic.subscription-events}",
                 groupId = "${spring.kafka.group.subscription-notification}")
  void listener(final String data) {
    try {
      // Extract the user id and email from the JSON payload
      final JsonNode jsonNode = objectMapper.readTree(data);
      final String userId = jsonNode.get("id").asText();
      final String email = jsonNode.get("email").asText();
      System.out.println("Processed subscription for user: " + userId);

      emailSenderService.sendEmailSubscription(email, userId);
    } catch (Exception e) {
      throw new NotificationServiceException(
          "Error processing subscription message: " + e.getMessage(), e);
    }
  }
}
