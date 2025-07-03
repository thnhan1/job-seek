package com.nhanab.demosecurity.service.impl;

import com.nhanab.demosecurity.dto.job.CreateJobPostCsvDto;
import com.nhanab.demosecurity.dto.job.CreateJobPostDto;
import com.nhanab.demosecurity.dto.job.ResponseJobPostDto;
import com.nhanab.demosecurity.dto.job.UpdateJobPostDto;
import com.nhanab.demosecurity.entity.JobPost;
import com.nhanab.demosecurity.entity.Major;
import com.nhanab.demosecurity.entity.StudentProfile;
import com.nhanab.demosecurity.entity.User;
import com.nhanab.demosecurity.exception.NotFoundException;
import com.nhanab.demosecurity.mapper.JobPostMapper;
import com.nhanab.demosecurity.repository.JobPostRepository;
import com.nhanab.demosecurity.repository.StudentProfileRepository;
import com.nhanab.demosecurity.repository.UserRepository;
import com.nhanab.demosecurity.repository.spec.JobPostsSpecifications;
import com.nhanab.demosecurity.security.UserDetailsImpl;
import com.nhanab.demosecurity.service.JobService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobPostMapper jobPostMapper;
    private final UserRepository userRepository;
    private final JobPostRepository jobPostRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Transactional
    @Override
    public ResponseJobPostDto create(CreateJobPostDto jobPostDto) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getId() == null) {
            throw new UsernameNotFoundException("User ID not found");
        }
       String role =  userDetails.getAuthorities().stream()
               .map(GrantedAuthority::getAuthority)
               .findFirst()
               .orElse(null);

        if ("ROLE_ADMIN".equals(role) || "ROLE_MODERATOR".equals(role)) {
            jobPostDto.setIsVerified(true);
        } else {
            jobPostDto.setIsVerified(false);
        }
        log.info("Role: {}", role);
        log.info("IsVerified: {}", jobPostDto.getIsVerified());

        JobPost jobPost = jobPostMapper.toEntity(jobPostDto);
        if (jobPost.getCreatedAt() == null) {
            jobPost.setCreatedAt(LocalDate.now());
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username " + userDetails.getUsername() + " not found"));
        jobPost.setUser(user);

        jobPost = jobPostRepository.save(jobPost);
        user.getJobPosts().add(jobPost);
        userRepository.save(user);

        return jobPostMapper.toDto(jobPost);
    }

    @Override
    public Page<ResponseJobPostDto> getJobPosts(String search, String major, Boolean isActive,Boolean isThisMonth, Boolean isThisWeek, Pageable pageable) {
        if (major != null && !major.isEmpty()) {
            major = major.replaceAll(" ", "_");
        }
        Specification<JobPost> spec = Specification.allOf(JobPostsSpecifications.isActive(isActive)).and(JobPostsSpecifications.hasJobTitle(search)).and(JobPostsSpecifications.hasMajor(major));

        if (Boolean.TRUE.equals(isThisMonth)) {
            spec = spec.and(JobPostsSpecifications.thisMonth());
        }
        if (Boolean.TRUE.equals(isThisWeek)) {
            spec = spec.and(JobPostsSpecifications.thisWeek());
        }

        return jobPostRepository.findAll(spec, pageable).map(jobPostMapper::toDto);
    }

    @Override
    public void saveJobPost(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getId() == null) {
            throw new NotFoundException("User not found");
        }

        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        JobPost jobPost = getJobIfExists(id);

        StudentProfile studentProfile = user.getStudentProfile();
        if (studentProfile == null) {
            throw new NotFoundException("Student profile not found");
        }
        studentProfile.getJobPosts().add(jobPost);
        userRepository.save(user);
    }

    @Override
    public Page<ResponseJobPostDto> getSavedJobPosts(Pageable pageable) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getId() == null) {
            throw new NotFoundException("User not found");
        }

        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        StudentProfile studentProfile = user.getStudentProfile();
        if (studentProfile == null) {
            throw new NotFoundException("Student profile not found");
        }
        Page<JobPost> jobPostPage = jobPostRepository.findAllByStudentProfileId(userDetails.getId(), pageable);
        return jobPostPage.map(jobPostMapper::toDto);
    }

    @Override
    public ResponseJobPostDto getJobPostById(Long id) {
        JobPost jobPost = getJobIfExists(id);
        return jobPostMapper.toDto(jobPost);
    }

    @Override
    public void update(Long id, UpdateJobPostDto jobPostDto) {
        JobPost jobPost =getJobIfExists(id);
        jobPostMapper.updateFromDto(jobPostDto, jobPost);
        jobPostRepository.save(jobPost);
    }




    @Override
    public void updateStatus(Long id, Boolean isActive) {
        JobPost jobPost = getJobIfExists(id);
        jobPost.setIsActive(isActive);
        jobPostRepository.save(jobPost);
    }


    @Override
    public void delete(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElseThrow(
                () -> new NotFoundException("Job post not found")
        );
       UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       if (userDetailsImpl.getId() == null) {
           throw new NotFoundException("User not found");
         }

        StudentProfile sf = studentProfileRepository.findById(userDetailsImpl.getId())
                .orElseThrow(() -> new NotFoundException("Student profile not found"));

       sf.getJobPosts().remove(jobPost);
        studentProfileRepository.save(sf);
    }

    private JobPost getJobIfExists(Long id) {
        return jobPostRepository.findById(id).orElseThrow(() -> new NotFoundException("Job post not found"));
    }


    @Override
    public void uploadFromCsv(MultipartFile file) {
        List<JobPost> jobPosts = new ArrayList<>();


        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();

            // Bỏ qua header row (dòng đầu tiên)
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);

                // Skip empty rows
                if (record.length == 0 || (record.length == 1 && record[0].trim().isEmpty())) {
                    continue;
                }

                JobPost jobPost = createJobPostFromCsvRecord(record);
                jobPosts.add(jobPost);
            }

            // Lưu tất cả vào database
             jobPostRepository.saveAll(jobPosts);
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private JobPost createJobPostFromCsvRecord(String[] record) {
        JobPost jobPost = new JobPost();

        try {
            // Mapping theo format: company_name,major,job_title,description,company_image_url,job_url,contact_email,location,expires_at
            jobPost.setCompanyName(getValueOrDefault(record, 0, ""));
            jobPost.setMajor(parseMajor(getValueOrDefault(record, 1, "")));
            jobPost.setJobTitle(getValueOrDefault(record, 2, ""));
            jobPost.setDescription(getValueOrDefault(record, 3, ""));
            jobPost.setCompanyImageUrl(getValueOrDefault(record, 4, ""));
            jobPost.setJobUrl(getValueOrDefault(record, 5, ""));
            jobPost.setLocation(getValueOrDefault(record, 6, ""));
            jobPost.setExpiresAt(parseDate(getValueOrDefault(record, 7, "")));

            // Set default values
            jobPost.setIsActive(true);
            jobPost.setCreatedAt(LocalDate.now());

        } catch (Exception e) {
            throw new RuntimeException("Error parsing CSV record: " + String.join(",", record), e);
        }

        return jobPost;
    }

    /**
     * Parse Major enum từ string
     */
    private Major parseMajor(String majorStr) {
        if (majorStr == null || majorStr.trim().isEmpty()) {
            return null;
        }

        try {
            // Remove spaces and convert to uppercase
            String cleanMajor = majorStr.trim().toUpperCase().replace(" ", "_");
            return Major.valueOf(cleanMajor);
        } catch (IllegalArgumentException e) {
            // Log warning và return null hoặc default value
            System.out.println("Warning: Invalid major value: " + majorStr);
            return null;
        }
    }

    /**
     * Parse LocalDate từ string
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr.trim(), formatter);
        } catch (Exception e) {
            System.out.println("Warning: Invalid date format: " + dateStr);
            return null;
        }
    }

    /**
     * Helper method để lấy giá trị từ array với index safety
     */
    private String getValueOrDefault(String[] record, int index, String defaultValue) {
        if (index >= 0 && index < record.length && record[index] != null) {
            return record[index].trim();
        }
        return defaultValue;
    }
}
