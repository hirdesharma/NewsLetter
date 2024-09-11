package com.example.user_subscription_service.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.user_subscription_service.dto.Subscription;
import com.example.user_subscription_service.dto.SubscriptionMessage;
import com.example.user_subscription_service.model.UserSubscription;
import com.example.user_subscription_service.repository.UserSubscriptionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

class SubscriptionMessageProcessorTest {

  @Mock
  private UserSubscriptionRepository userSubscriptionRepository;

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  @Mock
  private ExternalService externalService;

  @InjectMocks
  private SubscriptionMessageProcessor subscriptionMessageProcessor;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    objectMapper.findAndRegisterModules();
  }

  @Test
  void testPublishKafkaMessageSuccess() throws JsonProcessingException {
    Subscription subscription = new Subscription();
    subscription.setDuration(10L);

    UserSubscription userSubscription = new UserSubscription();
    userSubscription.setUserId(1L);
    userSubscription.setSubscriptionId(100L);

    SubscriptionMessage subscriptionMessage = new SubscriptionMessage();
    subscriptionMessage.setId(1L);

    when(externalService.fetchSubscriptionMessage(userSubscription.getUserId()))
        .thenReturn(subscriptionMessage);
    when(userSubscriptionRepository.save(any(UserSubscription.class)))
        .thenReturn(userSubscription);

    UserSubscription result = subscriptionMessageProcessor.publishKafkaMessage(subscription,
        userSubscription);

    assertNotNull(result);
    assertEquals(subscription.getDuration(), result.getDuration());
    assertTrue(result.getIsActive());
    assertEquals(LocalDate.now(), result.getStartDate());
    assertEquals(LocalDate.now().plusDays(subscription.getDuration()), result.getEndDate());

    verify(externalService, times(1)).fetchSubscriptionMessage(userSubscription.getUserId());
    verify(kafkaTemplate, times(1)).send(eq("subscription_events"), anyString());
    verify(userSubscriptionRepository, times(1)).save(userSubscription);
  }

  @Test
  void testPublishKafkaMessageSerializationError() throws JsonProcessingException {
    // Arrange
    Subscription subscription = new Subscription();
    subscription.setDuration(10L);

    UserSubscription userSubscription = new UserSubscription();
    userSubscription.setUserId(1L);
    userSubscription.setSubscriptionId(100L);

    SubscriptionMessage subscriptionMessage = new SubscriptionMessage();
    subscriptionMessage.setId(1L);

    when(externalService.fetchSubscriptionMessage(userSubscription.getUserId()))
        .thenReturn(subscriptionMessage);
    when(userSubscriptionRepository.save(any(UserSubscription.class)))
        .thenReturn(userSubscription);

    ObjectMapper mockObjectMapper = spy(objectMapper);
    doThrow(new JsonProcessingException("Serialization Error") {
    }).when(mockObjectMapper)
        .writeValueAsString(subscriptionMessage);

    subscriptionMessageProcessor = new SubscriptionMessageProcessor(userSubscriptionRepository,
        kafkaTemplate, externalService);

    assertDoesNotThrow(
        () -> subscriptionMessageProcessor.publishKafkaMessage(subscription, userSubscription));

    verify(externalService, times(1)).fetchSubscriptionMessage(userSubscription.getUserId());
    verify(userSubscriptionRepository, times(1)).save(userSubscription);
  }

  @Test
  void testPublishKafkaMessageNullSubscriptionMessage() {
    Subscription subscription = new Subscription();
    subscription.setDuration(10L);

    UserSubscription userSubscription = new UserSubscription();
    userSubscription.setUserId(1L);
    userSubscription.setSubscriptionId(100L);

    when(externalService.fetchSubscriptionMessage(userSubscription.getUserId()))
        .thenReturn(null);
    when(userSubscriptionRepository.save(any(UserSubscription.class)))
        .thenReturn(userSubscription);

    UserSubscription result = subscriptionMessageProcessor.publishKafkaMessage(subscription,
        userSubscription);

    assertNotNull(result);
    assertEquals(subscription.getDuration(), result.getDuration());
    assertTrue(result.getIsActive());

    verify(externalService, times(1)).fetchSubscriptionMessage(userSubscription.getUserId());
    verify(userSubscriptionRepository, times(1)).save(userSubscription);
  }
}
