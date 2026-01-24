package com.ltc.hrpayrollsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDto {
    private Long id;
    private String fullName;
    private String position;
    private LocalDate hireDate;
    private double baseSalary;
    private String email;
    private String phoneNumber;
    private String address;
    private Long departmentId;
}
