package com.sliit.studentplatform.common.security;

import com.sliit.studentplatform.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility component for generating and validating JWT tokens.
 *
 * <p>
 * Uses HMAC-SHA512 (HS512) signing. The secret is injected from environment
 * variables via {@link JwtConfig} — never hardcoded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

  private final JwtConfig jwtConfig;

  // ─────────────────────── Token Generation ────────────────────────────────

  /**
   * Generates an access token for the authenticated principal.
   *
   * @param authentication the Spring Security Authentication object
   * @return signed JWT access token string
   */
  public String generateAccessToken(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    return buildToken(userPrincipal.getId(), userPrincipal.getEmail(),
        jwtConfig.getExpiration());
  }

  /**
   * Generates a refresh token for the given user ID.
   *
   * @param userId the database ID of the user
   * @return signed JWT refresh token string
   */
  public String generateRefreshToken(Long userId) {
    return buildToken(userId, null, jwtConfig.getRefreshExpiration());
  }

  // ─────────────────────── Token Parsing ───────────────────────────────────

  /**
   * Extracts the user ID (subject) from a token.
   *
   * @param token the JWT string
   * @return the user ID stored as the token subject
   */
  public Long getUserIdFromToken(String token) {
    Claims claims = parseClaims(token);
    return Long.parseLong(claims.getSubject());
  }

  /**
   * Validates a JWT token's signature and expiry.
   *
   * @param token the JWT string
   * @return {@code true} if the token is valid and not expired
   */
  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException ex) {
      log.error("JWT validation failed: {}", ex.getMessage());
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty: {}", ex.getMessage());
    }
    return false;
  }

  // ─────────────────────── Private Helpers ─────────────────────────────────

  private String buildToken(Long userId, String email, long expiryMs) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expiryMs);

    var builder = Jwts.builder()
        .subject(String.valueOf(userId))
        .issuedAt(now)
        .expiration(expiry)
        .signWith(signingKey());

    if (email != null) {
      builder.claim("email", email);
    }

    return builder.compact();
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey signingKey() {
    return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
  }
}
