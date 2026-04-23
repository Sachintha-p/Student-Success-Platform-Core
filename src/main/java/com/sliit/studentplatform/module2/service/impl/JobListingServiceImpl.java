package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.auth.repository.UserRepository;
import com.sliit.studentplatform.common.exception.ResourceNotFoundException;
import com.sliit.studentplatform.module2.dto.request.JobListingRequest;
import com.sliit.studentplatform.module2.dto.response.JobListingResponse;
import com.sliit.studentplatform.module2.entity.JobListing;
import com.sliit.studentplatform.module2.repository.JobListingRepository;
import com.sliit.studentplatform.module2.service.interfaces.IJobListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobListingServiceImpl implements IJobListingService {

    private final JobListingRepository jobListingRepository;
    private final UserRepository userRepository;

    @Override
    public JobListingResponse createJob(JobListingRequest request, Long userId) {
        // Fetch the admin user who is creating this job
        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        JobListing job = JobListing.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .type(request.getType())
                .location(request.getLocation())
                .remote(request.isRemote())
                .deadline(request.getDeadline())
                .postedBy(admin) // Links the job to the admin to fix the SQL error
                .active(true)    // FIXED: Changed from .isActive(true) to .active(true)
                .build();

        return mapToResponse(jobListingRepository.save(job));
    }

    @Override
    public JobListingResponse updateJob(Long id, JobListingRequest request) {
        JobListing job = jobListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Listing not found with id: " + id));

        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setType(request.getType());
        job.setLocation(request.getLocation());
        job.setRemote(request.isRemote());
        job.setDeadline(request.getDeadline());

        return mapToResponse(jobListingRepository.save(job));
    }

    @Override
    public void deleteJob(Long id) {
        JobListing job = jobListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Listing not found with id: " + id));
        jobListingRepository.delete(job);
    }

    @Override
    public List<JobListingResponse> getAllJobs() {
        return jobListingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobListingResponse getJobById(Long id) {
        JobListing job = jobListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Listing not found with id: " + id));

        return mapToResponse(job);
    }

    private JobListingResponse mapToResponse(JobListing entity) {
        return JobListingResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .company(entity.getCompany())
                .description(entity.getDescription())
                .requiredSkills(entity.getRequiredSkills())
                .type(entity.getType())
                .location(entity.getLocation())
                .remote(entity.isRemote())
                .deadline(entity.getDeadline())
                .active(entity.isActive())
                .postedById(entity.getPostedBy() != null ? entity.getPostedBy().getId() : null)
                .build();
    }
}