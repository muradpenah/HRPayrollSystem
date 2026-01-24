package com.ltc.hrpayrollsystem.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDto {
    @NotNull
    @Size(min = 3, max = 50)
    private String fullName;
    @NotNull
    @Size(min = 3, max = 10)
    private String position;
    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
    @NotNull
    @Positive
    private double baseSalary;
    @Email(message = "Invalid email format!")
    private String email;
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    @NotEmpty
    private String address;
    private Long departmentId;
}
