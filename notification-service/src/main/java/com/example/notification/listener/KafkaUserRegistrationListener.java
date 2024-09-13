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
public class KafkaUserRegistrationListener {

  private final EmailSenderService emailSenderService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * This method is the entry point for the Kafka Consumer. It listens on the topic
   * specified in the application.properties file and processes the message by
   * sending an email to the user.
   *
   * @param data the raw message received from Kafka
   */
  @KafkaListener(topics = "${spring.kafka.topic.registrationNotify}",
                 groupId = "${spring.kafka.group.notification-group}")
  void listener(String data) {
    try {
      // Extract the user id and email from the JSON payload
      final JsonNode jsonNode = objectMapper.readTree(data);
      final String userId = jsonNode.get("id").asText();
      final String email = jsonNode.get("email").asText();

      System.out.println("Processed subscription for user: " + userId);

      emailSenderService.sendEmailRegistration(email);
    } catch (Exception e) {
      throw new NotificationServiceException(
          "Error processing subscription message: " + e.getMessage(), e);
    }
  }
}
