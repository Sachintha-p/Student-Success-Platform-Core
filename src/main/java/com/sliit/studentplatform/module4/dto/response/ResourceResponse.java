package com.sliit.studentplatform.module4.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {
  private Long id;
  private String title;
  private String description;
  private String subject;
  private String type;
  private String url;
  private String[] tags;
  private boolean bookmarked;
}
