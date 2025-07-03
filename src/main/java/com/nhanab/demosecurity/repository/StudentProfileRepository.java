package com.nhanab.demosecurity.repository;

import com.nhanab.demosecurity.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

  Optional<StudentProfile> findByUser_Id(Long id);
}