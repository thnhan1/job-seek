package com.nhanab.demosecurity.dto.job;

import com.nhanab.demosecurity.entity.Major;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ResponseJobPostDto {
    private Long id;
    private String companyName;

    @Enumerated(EnumType.STRING)
    private Major major;

    private String jobTitle;

    private String description;

    private String companyImageUrl;

    private String jobUrl;

    private String jobType;

    private String location;

    private Boolean isActive;

    private LocalDate createdAt;

    private LocalDate expiresAt;
}
