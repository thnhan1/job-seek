package com.nhanab.demosecurity.service;

import com.nhanab.demosecurity.dto.job.CreateJobPostDto;
import com.nhanab.demosecurity.dto.job.ResponseJobPostDto;
import com.nhanab.demosecurity.dto.job.UpdateJobPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

public interface JobService {
    ResponseJobPostDto create(CreateJobPostDto jobPostDto);

    Page<ResponseJobPostDto> getJobPosts(String search, String major,Boolean isActive, Boolean isThisMonth, Boolean isThisWeek, Pageable pageable);

    void saveJobPost(Long id);
   Page<ResponseJobPostDto> getSavedJobPosts(Pageable pageable);

    ResponseJobPostDto getJobPostById(@PathVariable Long id);

    void update(Long id, UpdateJobPostDto jobPostDto);

    void delete(Long jobPostId);

    void updateStatus(Long jobPostId, Boolean isActive);

    void uploadFromCsv(MultipartFile file);
}
