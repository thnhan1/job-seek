package com.nhanab.demosecurity.dto;

import com.nhanab.demosecurity.entity.Major;
import lombok.Data;

@Data
public class StudentSignUpRequest {
    private String studentId;
    private String fullName;
    private String email;
    private String password;
    private Major major;
}
