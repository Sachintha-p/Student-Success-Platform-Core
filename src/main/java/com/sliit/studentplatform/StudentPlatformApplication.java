package com.sliit.studentplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the SLIIT Student Success Platform backend.
 *
 * <p>
 * Enables:
 * <ul>
 * <li>JPA Auditing — auto-populates {@code createdAt}/{@code updatedAt} on
 * entities</li>
 * <li>Async — for async notification dispatch</li>
 * </ul>
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class StudentPlatformApplication {

  public static void main(String[] args) {
    SpringApplication.run(StudentPlatformApplication.class, args);
  }
}
