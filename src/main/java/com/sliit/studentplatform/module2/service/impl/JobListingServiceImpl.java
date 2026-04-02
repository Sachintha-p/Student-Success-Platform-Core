package com.sliit.studentplatform.module2.service.impl;

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

    @Override
    public JobListingResponse createJob(JobListingRequest request) {
        JobListing job = JobListing.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .type(request.getType())
                .location(request.getLocation())
                .remote(request.isRemote())
                .deadline(request.getDeadline())
                .build();

        return mapToResponse(jobListingRepository.save(job));
    }

    @Override
    public JobListingResponse updateJob(Long id, JobListingRequest request) {
        JobListing job = jobListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

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
        jobListingRepository.deleteById(id);
    }

    @Override
    public List<JobListingResponse> getAllJobs() {
        return jobListingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // THE FIX: Implementation of the method from your interface
    // ==========================================================
    @Override
    public JobListingResponse getJobById(Long id) {
        JobListing job = jobListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Listing not found with id: " + id));

        return mapToResponse(job);
    }

    /**
     * Helper method to map the JobListing Entity
     * to the JobListingResponse DTO.
     */
    private JobListingResponse mapToResponse(JobListing entity) {
        return JobListingResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .company(entity.getCompany())
                .description(entity.getDescription())
                .requiredSkills(entity.getRequiredSkills()) // Matches String[] type
                .type(entity.getType())
                .location(entity.getLocation())
                .remote(entity.isRemote())
                .deadline(entity.getDeadline())
                .active(entity.isActive())
                .postedById(entity.getPostedBy() != null ? entity.getPostedBy().getId() : null)
                .build();
    }
}