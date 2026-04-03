package com.sliit.studentplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration.
 * CORS is already handled by CorsConfig and SecurityConfig.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Other WebMvc configuration can be added here if needed
}