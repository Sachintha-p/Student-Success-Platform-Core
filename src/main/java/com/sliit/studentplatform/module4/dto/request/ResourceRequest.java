package com.sliit.studentplatform.module4.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequest {
  @NotBlank(message = "Title is required")
  @jakarta.validation.constraints.Size(min = 3, max = 200)
  private String title;

  @NotBlank(message = "URL is required")
  @org.hibernate.validator.constraints.URL(message = "Invalid URL format")
  private String url;

  private String description;

  @NotBlank(message = "Subject is required")
  private String subject;

  @NotBlank(message = "Type is required")
  private String type;

  private java.util.List<String> tags;
}
