package com.sliit.studentplatform.module3.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMilestoneRequest {
  @NotNull
  private Long groupId;
  @NotBlank
  @Size(max = 200)
  private String title;
  @Size(max = 2000)
  private String description;
  @FutureOrPresent
  private LocalDate dueDate;
}
