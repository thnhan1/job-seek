package com.nhanab.demosecurity.dto;

import com.nhanab.demosecurity.entity.Major;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileDto implements Serializable {
    private long id;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private String studentId;
    private String phone;
    private String major;
    private String year;
}
