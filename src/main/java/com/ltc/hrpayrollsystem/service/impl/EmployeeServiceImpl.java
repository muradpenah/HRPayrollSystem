package com.ltc.hrpayrollsystem.service.impl;

import com.ltc.hrpayrollsystem.dto.request.EmployeeRequestDto;
import com.ltc.hrpayrollsystem.dto.response.EmployeeResponseDto;
import com.ltc.hrpayrollsystem.repo.EmployeeRepo;
import com.ltc.hrpayrollsystem.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepo employeeRepo;

    @Override
    public EmployeeResponseDto saveEmployee(EmployeeRequestDto employeeRequestDto) {
        return null;
    }

    @Override
    public Page<EmployeeResponseDto> getAllEmployees(Pageable pageable) {
        return null;
    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {
        return null;
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        return null;
    }

    @Override
    public void deleteEmployee(Long id) {

    }

    @Override
    public List<EmployeeResponseDto> findByDepartmentId(Long departmentId) {
        return List.of();
    }
}
