package com.ltc.hrpayrollsystem.dto.response;


import com.ltc.hrpayrollsystem.enumaration.DepartmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponseDto {
    private Long id;

    private String departmentName;

    private String departmentAddress;

    private DepartmentStatus status;
}
