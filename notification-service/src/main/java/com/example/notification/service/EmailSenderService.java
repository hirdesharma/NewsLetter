package com.example.notification.service;

import com.example.notification.exception.NotificationServiceException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

  private final JavaMailSender javaMailSender;
  @Value("${spring.mail.username}")
  private String fromEmail;

  public final void sendEmail(final String toEmail, final String body) {
    try {
      if (Objects.isNull(toEmail) || toEmail.trim().isEmpty()) {
        System.out.println("Email address is invalid.");
        return;
      }
      final SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(toEmail);
      message.setSubject("Subscription done");
      message.setText(body);
      javaMailSender.send(message);
      System.out.println("chala gaya mail 🫡🫡");
    } catch (Exception e) {
      throw new NotificationServiceException("Error sending email to: " + toEmail, e);

    }
  }
}
