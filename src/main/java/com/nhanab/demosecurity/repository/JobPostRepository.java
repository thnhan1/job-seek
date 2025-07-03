package com.nhanab.demosecurity.repository;

import com.nhanab.demosecurity.entity.JobPost;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {
   @Query("select jp from StudentProfile j join j.jobPosts jp where j.id = :id")
   Page<JobPost> findAllByStudentProfileId(@org.springframework.data.repository.query.Param("id") Long id, Pageable pageable);
}
