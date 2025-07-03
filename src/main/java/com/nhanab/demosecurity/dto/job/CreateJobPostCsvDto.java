package com.nhanab.demosecurity.dto.job;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
// Định nghĩa DTO cho map dữ liệu csv
public class CreateJobPostCsvDto implements Serializable {
    @CsvBindByName(column = "companyName")
    private String companyName;
    @CsvBindByName(column = "major")
    private String major;
    @CsvBindByName(column = "jobTitle")
    private String jobTitle;
    @CsvBindByName(column = "description")
    private String description;
    @CsvBindByName(column = "companyImageUrl")
    private String companyImageUrl;
    @CsvBindByName(column = "jobUrl")
    private String jobUrl;
    @CsvBindByName(column = "contactEmail")
    private String contactEmail;
    @CsvBindByName(column = "location")
    private String location;
    @CsvBindByName(column = "expiresAt")
    private String expiresAt;
    // getter/setter
}