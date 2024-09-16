package com.example.subscription_service.service;

import com.example.subscription_service.context.JwtContextHolder;
import com.example.subscription_service.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
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
public class ExternalService implements ExternalServiceInterface {

  private final RestTemplate restTemplate;

  /**
   * Fetches the user details using the JWT token provided in the {@link JwtContextHolder}.
   *
   * @return The user details.
   * @throws RuntimeException if the user is not authenticated or if there's an error fetching the user details.
   */
  @Override
  public User fetchUserDetails() {
    String jwtToken = JwtContextHolder.getJwtToken();

    try {
      // Create a new HTTP entity with the JWT token in the Authorization header.
      final HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + jwtToken);

      final HttpEntity<String> entity = new HttpEntity<>(headers);

      // Make a GET request to the user service to fetch the user details.
      final ResponseEntity<User> userResponse = restTemplate.exchange(
          "http://localhost:8080/api/users/auth",
          HttpMethod.GET,
          entity,
          new ParameterizedTypeReference<User>() {}
      );

      // Return the user details.
      return userResponse.getBody();
    } catch (HttpClientErrorException.Unauthorized e) {
      // If the user is not authenticated, throw a RuntimeException with the appropriate message.
      throw new RuntimeException("User is not authenticated: Unauthorized", e);
    } catch (RestClientException e) {
      // If there's an error fetching the user details, throw a RuntimeException with the appropriate message.
      throw new RuntimeException("Failed to fetch user details", e);
    }
  }
}
