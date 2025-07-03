package com.nhanab.demosecurity.dto.job;

import com.nhanab.demosecurity.entity.Major;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateJobPostDto implements Serializable {
    private String companyName;

    @Enumerated(EnumType.STRING)
    private Major major;

    private String jobTitle;

    private String companyImageUrl;

    private String jobUrl;

    private String description;

    private Boolean isVerified;

    private String jobType;

    private String location;

    private LocalDate createdAt;
    private LocalDate expiresAt;
}
