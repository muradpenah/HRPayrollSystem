package com.ltc.hrpayrollsystem.service;

import com.ltc.hrpayrollsystem.dto.request.DepartmentRequestDto;
import com.ltc.hrpayrollsystem.dto.response.DepartmentResponseDto;

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
}
