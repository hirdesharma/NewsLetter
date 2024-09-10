package com.example.subscription_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExternalService {

  private final RestTemplate restTemplate;

  public final boolean fetchSubscription(String jwtToken) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + jwtToken);

      HttpEntity<String> entity = new HttpEntity<>(headers);

      ResponseEntity<Boolean> response = restTemplate.exchange(
          "http://localhost:8080/api/users/auth",
          HttpMethod.GET,
          entity,
          Boolean.class
      );

      return Boolean.TRUE.equals(response.getBody());
    } catch (HttpClientErrorException.Unauthorized e) {
      throw new RuntimeException("User is not authenticated: Unauthorized", e);
    } catch (RestClientException e) {
      throw new RuntimeException("Failed to fetch user authentication", e);
    }
  }
}
