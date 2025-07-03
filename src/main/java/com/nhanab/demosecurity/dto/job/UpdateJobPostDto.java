package com.nhanab.demosecurity.dto.job;

import com.nhanab.demosecurity.entity.Major;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UpdateJobPostDto implements Serializable {
    private String companyName;

    private Major major;

    private String jobTitle;

    private String companyImageUrl;

    private String jobUrl;

    private String description;

    private String contactEmail;

    private String location;

    private LocalDate expiresAt;
}
