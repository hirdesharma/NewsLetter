package com.example.user_service.jwtconfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  public Long extractUserId(String token) {
    return Long.parseLong(extractClaim(token, Claims::getSubject));
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(Long userId, String role) {
    try {
      return Jwts.builder()
          .setSubject(Long.toString(userId))
          .claim("role", role)
          .setIssuedAt(new Date(System.currentTimeMillis()))
          .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
          .signWith(SignatureAlgorithm.HS256, secret)
          .compact();
    } catch (Exception e) {
      throw new RuntimeException("Error generating token: " + e.getMessage());
    }
  }

  public Boolean validateToken(String token, Long userId) {
    final Long extractedUserId = extractUserId(token);
    return (extractedUserId.equals(userId) && !isTokenExpired(token));
  }

  public String extractRole(String token) {
    return (String) extractClaim(token, claims -> claims.get("role"));
  }
}