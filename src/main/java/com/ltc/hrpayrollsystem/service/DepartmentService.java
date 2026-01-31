package com.ltc.hrpayrollsystem.service;

import com.ltc.hrpayrollsystem.dto.request.DepartmentRequestDto;
import com.ltc.hrpayrollsystem.dto.response.DepartmentResponseDto;
import com.ltc.hrpayrollsystem.enumaration.DepartmentStatus;

import java.util.List;

public interface DepartmentService {
    //C
    DepartmentResponseDto createDepartment(DepartmentRequestDto departmentRequestDto);
    //R
    DepartmentResponseDto getDepartmentById(Long id);
    List<DepartmentResponseDto> getAllDepartments();
    //U
    DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto departmentRequestDto);
    //D
    void deleteDepartment(Long id);
    //Patch
    DepartmentResponseDto changeDepartmentStatus(Long id, DepartmentStatus newStatus);
    //Query
    long countByDepartmentId(Long departmentId);
    double calculateTotalSalaryByDepartment(Long departmentId);
    double calculateAverageSalaryByDepartment(Long departmentId);

}
