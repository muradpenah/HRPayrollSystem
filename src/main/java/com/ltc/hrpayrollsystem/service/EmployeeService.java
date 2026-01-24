package com.ltc.hrpayrollsystem.service;

import com.ltc.hrpayrollsystem.dto.request.EmployeeRequestDto;
import com.ltc.hrpayrollsystem.dto.response.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    //C
    EmployeeResponseDto saveEmployee(EmployeeRequestDto employeeRequestDto);
    //R
    Page<EmployeeResponseDto> getAllEmployees(Pageable pageable);
    EmployeeResponseDto getEmployeeById(Long id);
    //U
    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto employeeRequestDto);
    //D
    void deleteEmployee(Long id);
    //Query
    List<EmployeeResponseDto> findByDepartmentId(Long departmentId);
}
