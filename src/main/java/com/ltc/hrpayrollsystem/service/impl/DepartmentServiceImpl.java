package com.ltc.hrpayrollsystem.service.impl;

import com.ltc.hrpayrollsystem.dto.request.DepartmentRequestDto;
import com.ltc.hrpayrollsystem.dto.response.DepartmentResponseDto;
import com.ltc.hrpayrollsystem.entity.Department;
import com.ltc.hrpayrollsystem.exception.DepartmentNotFoundException;
import com.ltc.hrpayrollsystem.repo.DepartmentRepo;
import com.ltc.hrpayrollsystem.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepo departmentRepo;
    @Override
    public DepartmentResponseDto createDepartment(DepartmentRequestDto departmentRequestDto) {
        Department department = new Department();
        department.setDepartmentName(departmentRequestDto.getDepartmentName());
        department.setDepartmentAddress(departmentRequestDto.getDepartmentAddress());
        Department saved = departmentRepo.save(department);
        return new DepartmentResponseDto(saved.getId(),saved.getDepartmentName(), saved.getDepartmentAddress(), saved.getStatus());
    }

    @Override
    public DepartmentResponseDto getDepartmentById(Long id) {
        Department department = departmentRepo.findById(id).orElseThrow(()->
                new DepartmentNotFoundException("Department not found" + id));
        DepartmentResponseDto departmentResponseDto = new DepartmentResponseDto();
        departmentResponseDto.setId(department.getId());
        departmentResponseDto.setDepartmentName(department.getDepartmentName());
        departmentResponseDto.setStatus(department.getStatus());
        return departmentResponseDto;
    }

    @Override
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentRepo.findAll().stream().map(department ->
                new DepartmentResponseDto(
                        department.getId(),
                        department.getDepartmentName(),
                        department.getDepartmentAddress(),
                        department.getStatus())).toList();
    }

    @Override
    public DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto departmentRequestDto) {
        Department exist = departmentRepo.findById(id).orElseThrow(()->
                new DepartmentNotFoundException("Department not found" + id));
        exist.setDepartmentName(departmentRequestDto.getDepartmentName());
        exist.setDepartmentAddress(departmentRequestDto.getDepartmentAddress());
        Department saved = departmentRepo.save(exist);
        return new DepartmentResponseDto(saved.getId(),saved.getDepartmentName(),saved.getDepartmentAddress(),saved.getStatus());
    }

    @Override
    public void deleteDepartment(Long id) {
        if(!departmentRepo.existsById(id)){
            throw new DepartmentNotFoundException("Department not found" + id);
        }
        departmentRepo.deleteById(id);
    }
}
