package com.sliit.studentplatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 3 configuration.
 *
 * <p>
 * Registers a Bearer JWT security scheme so that Swagger UI shows an
 * "Authorize" button and sends the token on protected requests.
 */
@Configuration
public class SwaggerConfig {

  private static final String SECURITY_SCHEME_NAME = "BearerAuth";

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(apiInfo())
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .components(new Components()
            .addSecuritySchemes(SECURITY_SCHEME_NAME,
                new SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Provide a valid JWT access token obtained from /api/v1/auth/login")));
  }

  private Info apiInfo() {
    return new Info()
        .title("SLIIT Student Success Platform API")
        .description("REST API for the Student Success Platform — 4 modules: " +
            "Team Matchmaker, Job ATS, Campus Engagement Hub, AI Academic Assistant")
        .version("1.0.0")
        .contact(new Contact()
            .name("SLIIT Development Team")
            .email("dev@sliit.lk")
            .url("https://www.sliit.lk"))
        .license(new License()
            .name("MIT")
            .url("https://opensource.org/licenses/MIT"));
  }
}
