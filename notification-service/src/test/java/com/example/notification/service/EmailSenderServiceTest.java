package com.example.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.notification.exception.NotificationServiceException;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class EmailSenderServiceTest {

  @Mock
  private JavaMailSender javaMailSender;

  @InjectMocks
  private EmailSenderService emailSenderService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSendEmail_Success() {
    String toEmail = "test@example.com";
    String body = "Test email body";

    emailSenderService.sendEmail(toEmail, body);

    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(javaMailSender).send(messageCaptor.capture());

    SimpleMailMessage sentMessage = messageCaptor.getValue();
    assertEquals("test@example.com", Objects.requireNonNull(sentMessage.getTo())[0]);
    assertEquals("Subscription done", sentMessage.getSubject());
    assertEquals(body, sentMessage.getText());
  }

  @Test
  void testSendEmail_InvalidEmail() {
    String invalidEmail = "   ";

    emailSenderService.sendEmail(invalidEmail, "Some body");

    verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
  }

  @Test
  void testSendEmail_ExceptionHandling() {
    String toEmail = "test@example.com";
    String body = "Test email body";
    doThrow(new RuntimeException("Mail sending failed")).when(javaMailSender).send(any(SimpleMailMessage.class));

    NotificationServiceException thrown = assertThrows(NotificationServiceException.class, () -> {
      emailSenderService.sendEmail(toEmail, body);
    });
    assertEquals("Error sending email to: test@example.com", thrown.getMessage());
    assertNotNull(thrown.getCause());
  }
}
