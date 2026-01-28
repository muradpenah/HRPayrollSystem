package com.ltc.hrpayrollsystem.controller;

import com.ltc.hrpayrollsystem.dto.request.DepartmentRequestDto;
import com.ltc.hrpayrollsystem.dto.response.DepartmentResponseDto;
import com.ltc.hrpayrollsystem.enumaration.DepartmentStatus;
import com.ltc.hrpayrollsystem.service.impl.DepartmentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@AllArgsConstructor
@Tag(name = "Departments Operation panel")
public class DepartmentController {
    private final DepartmentServiceImpl departmentService;

    @PostMapping("/create")
    @Operation(summary = "Department creating operation")
    public ResponseEntity<DepartmentResponseDto> create(@RequestBody DepartmentRequestDto departmentRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.createDepartment(departmentRequestDto));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Departments fetching operation")
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartments(){
        return ResponseEntity.status(HttpStatus.OK).body(departmentService.getAllDepartments());
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Department fetching operation by ID")
    public ResponseEntity<DepartmentResponseDto> getDepartmentById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(departmentService.getDepartmentById(id));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Department updating operation by ID")
    public ResponseEntity<DepartmentResponseDto> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequestDto departmentRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(departmentService.updateDepartment(id,departmentRequestDto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Department status changing operation by ID")
    public ResponseEntity<DepartmentResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam DepartmentStatus status) {
        return ResponseEntity.status(HttpStatus.OK).body(departmentService.changeDepartmentStatus(id, status));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Department deleting operation by ID")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id){
        departmentService.deleteDepartment(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department deleted successfully by ID: "+id);
    }
}
