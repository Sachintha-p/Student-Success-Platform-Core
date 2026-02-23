package com.sliit.studentplatform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds JWT properties from {@code application*.properties} under the
 * {@code app.jwt} prefix.
 *
 * <p>
 * Never hardcode these values — they are injected via environment variables at
 * runtime.
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtConfig {

  /** HS512 signing secret — must be at least 64 characters (512-bit key). */
  private String secret;

  /** Access-token lifetime in milliseconds (default 24 h = 86 400 000 ms). */
  private long expiration;

  /** Refresh-token lifetime in milliseconds (default 7 days = 604 800 000 ms). */
  private long refreshExpiration;
}
