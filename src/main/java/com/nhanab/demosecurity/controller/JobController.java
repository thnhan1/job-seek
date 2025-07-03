package com.nhanab.demosecurity.controller;

import com.nhanab.demosecurity.dto.MessageResponse;
import com.nhanab.demosecurity.dto.PageResponse;
import com.nhanab.demosecurity.dto.job.CreateJobPostDto;
import com.nhanab.demosecurity.dto.job.JobPostStatusUpdateRequest;
import com.nhanab.demosecurity.dto.job.ResponseJobPostDto;
import com.nhanab.demosecurity.dto.job.UpdateJobPostDto;
import com.nhanab.demosecurity.repository.UserRepository;
import com.nhanab.demosecurity.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Job", description = "Job post management APIs")
public class JobController {

    private final UserRepository userRepository;
    private final JobService jobService;

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Create a new job post",
        description = "Creates a new job post with the provided details"
    )
    @PostMapping
    public ResponseEntity<ResponseJobPostDto> create(@RequestBody CreateJobPostDto jobPostDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(jobPostDto));
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Save a job post",
        description = "Save a job post by its ID"
    )
    @PostMapping("/{id}/save")
    public ResponseEntity<Void> saveJobPost(@PathVariable Long id) {
        jobService.saveJobPost(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get saved job posts",
        description = "Retrieve a paginated list of saved job posts"
    )
    @GetMapping("/saved")
    public ResponseEntity<PageResponse<ResponseJobPostDto>> getSavedJobPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ResponseJobPostDto> dtos = jobService.getSavedJobPosts(pageable);
        return ResponseEntity.ok(new PageResponse<>(dtos));
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get a job post by ID",
        description = "Retrieve a job post by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ResponseJobPostDto> getJob(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(jobService.getJobPostById(id));
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get job posts",
        description = "Retrieve a paginated list of job posts with optional filters"
    )
    @GetMapping
    public ResponseEntity<PageResponse<ResponseJobPostDto>> getJobPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(required = false) String major,
            @RequestParam(defaultValue = "true") Boolean isActive,
            @RequestParam(defaultValue = "false") Boolean isThisMonth,
            @RequestParam(defaultValue = "false") Boolean isThisWeek,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        log.info("Fetching job posts with title: {}, major: {}, isActive: {}, isThisMonth: {}, isThisWeek: {}, sortBy: {}, sortDir: {}",
                title, major, isActive, isThisMonth, isThisWeek, sortBy, sortDir);

        String validTitle = StringUtils.hasText(title) ? title : null;
        String validMajor = StringUtils.hasText(major) ? major : null;

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ResponseJobPostDto> dtos = jobService.getJobPosts(validTitle, validMajor, isActive, isThisMonth, isThisWeek, pageable);
        return ResponseEntity.ok(new PageResponse<>(dtos));
    }


    @io.swagger.v3.oas.annotations.Operation(
        summary = "Delete a job post",
        description = "Delete a job post by its ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        jobService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Update a job post",
        description = "Update a job post by its ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody UpdateJobPostDto jobPostDto) {
        jobService.update(id, jobPostDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Deactivate a job post",
        description = "Deactivate a job post by its ID"
    )
    @PatchMapping("{id}/deactive")
    public ResponseEntity<MessageResponse> unActiveJobPost(@PathVariable Long id,
                                                           @RequestBody JobPostStatusUpdateRequest request) {
        jobService.updateStatus(id, request.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse("job id: " + id + "Status updated successfully!")
        );
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Bulk upload job posts",
        description = "Upload multiple job posts from a CSV file"
    )
    @PostMapping("bulk-upload")
    public ResponseEntity<?> bulkUploadJobPosts(
            @RequestParam("file")MultipartFile file
            ) {
        jobService.uploadFromCsv(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new MessageResponse("Job posts uploaded successfully!")
        );
    }
}