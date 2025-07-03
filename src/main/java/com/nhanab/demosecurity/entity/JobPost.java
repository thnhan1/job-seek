package com.nhanab.demosecurity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "job_posts")
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String companyName;

    @Enumerated(EnumType.STRING)
    private Major major;

    private String jobTitle;

    private String jobType;

    private Boolean isVerified;

    @Column(length = 10000)
    private String description;

    @Column(length = 2000)
    private String companyImageUrl;

    @Column(length = 2000)
    private String jobUrl;

    private String location;

    private Boolean isActive = true;

    private LocalDate createdAt;

    private LocalDate expiresAt;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "jobPosts")
    private List<StudentProfile> savedByStudents = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "created_user_id")
    private User user;
}
