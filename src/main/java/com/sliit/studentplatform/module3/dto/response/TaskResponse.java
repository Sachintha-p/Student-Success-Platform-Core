package com.sliit.studentplatform.module3.dto.response;

import com.sliit.studentplatform.module3.enums.TaskPriority;
import com.sliit.studentplatform.module3.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Integer position;
    private Long projectId;
    private Long milestoneId;
    private Long assignedToId;
    private Long createdById;
    private boolean isOverdue;
    private boolean isUpcoming;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
