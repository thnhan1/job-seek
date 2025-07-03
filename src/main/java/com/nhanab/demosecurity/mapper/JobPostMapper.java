package com.nhanab.demosecurity.mapper;

import com.nhanab.demosecurity.dto.job.CreateJobPostDto;
import com.nhanab.demosecurity.dto.job.ResponseJobPostDto;
import com.nhanab.demosecurity.dto.job.UpdateJobPostDto;
import com.nhanab.demosecurity.entity.JobPost;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface JobPostMapper {
    JobPost toEntity(CreateJobPostDto jobPostDto);

    ResponseJobPostDto toDto(JobPost jobPost);

    void updateFromDto(UpdateJobPostDto jobPostDto, @MappingTarget JobPost jobPost);
}
