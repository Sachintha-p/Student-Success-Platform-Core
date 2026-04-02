package com.sliit.studentplatform.module3.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Date and time are required")
    private LocalDateTime eventDate;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotBlank(message = "Category is required")
    private String category;
    
    private Long organizerId;

    @Min(value = 1, message = "Maximum attendees must be at least 1")
    private Integer maxAttendees;

    private Boolean isOnline;
    private Boolean isPublished;
}
