package com.ltc.hrpayrollsystem.controller;

import com.ltc.hrpayrollsystem.dto.PositionStatsDTO;
import com.ltc.hrpayrollsystem.dto.request.EmployeeRequestDto;
import com.ltc.hrpayrollsystem.dto.response.EmployeeResponseDto;
import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import com.ltc.hrpayrollsystem.service.impl.EmployeeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;



@RestController
@RequestMapping("/employees")
@AllArgsConstructor
@Tag(name = "Employees Operation panel")
public class EmployeeController {
    private final EmployeeServiceImpl employeeService;

    @PostMapping(path = "/create")
    @Operation(summary = "Employees creations operation", description = " if couldn't find employeee , problem was handled by validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee created successfully"),
            @ApiResponse(responseCode = "404", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmployeeResponseDto> saveEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.saveEmployee(employeeRequestDto));
    }

    @ApiResponse(responseCode = "204", description = "Employees fetched successfully")
    @Operation(summary = "Employees fetching operation", description = "if couldn't find employee , problem was handled by validation")
    @GetMapping(path = "/getALl")
    public ResponseEntity<Page<EmployeeResponseDto>> getAllEmployees(@ParameterObject
                                                                     @PageableDefault(page = 0, size = 5, value = 10, sort = "id",
                                                                             direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<EmployeeResponseDto> responseDtos = employeeService.getAllEmployees(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping(path = "/get/{id}")
    @Operation(summary = "Employee fetching operation by ID", description = "if couldn't find employee , problem was handled by validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeById(id));
    }


    @PutMapping(path = "/update/{id}")
    @Operation(summary = "Employee updating operation by ID ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmployeeResponseDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDto employeeRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.updateEmployee(id, employeeRequestDto));
    }

    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Employee deleting operation", description = "if couldn't find employee , problem was handled by validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee deleted successfully by ID: " + id);
    }

    @GetMapping(path = "/getDepartment/{departmentId}")
    public ResponseEntity<List<EmployeeResponseDto>> findByDepartmentId(@PathVariable Long departmentId) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findByDepartmentId(departmentId));
    }

    @GetMapping(path = "/searchByKeyword")
    public ResponseEntity<List<EmployeeResponseDto>> searchByKeyword(@RequestParam String keyword){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.searchByName(keyword));
    }

    @GetMapping(path="/filterBySalary")
    public ResponseEntity<List<EmployeeResponseDto>> filterBySalary(@RequestParam Long departmentId, @RequestParam Double minSalary,@RequestParam Double maxSalary){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findByDepartmentAndSalaryRange(departmentId,minSalary,maxSalary));
    }

    @GetMapping(path = "/topEarner")
    public ResponseEntity<List<EmployeeResponseDto>> topEarnerByDepartmentId(@RequestParam Long departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findTopEarners(departmentId));
    }

    @GetMapping(path = "/checkEmails")
    public ResponseEntity<List<EmployeeResponseDto>> checkEmail(@RequestParam Long departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findEmployeesWithNonCorporateEmails(departmentId));
    }

    @GetMapping(path = "/sortSalaryByASC")
    public ResponseEntity<List<EmployeeResponseDto>> sortSalaryByASC(@RequestParam Long departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findAllOrderByNetSalaryAsc(departmentId));
    }

    @GetMapping(path = "/sortSalaryByDESC")
    public ResponseEntity<List<EmployeeResponseDto>> sortSalaryByDESC(@RequestParam Long departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findAllOrderByNetSalaryDesc(departmentId));
    }

    @GetMapping(path ="/sortNameByASC")
    public ResponseEntity<List<EmployeeResponseDto>> sortNameByASC(@RequestParam Long departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findAllOrderedByFullNameAsc(departmentId));
    }

    @GetMapping(path ="/sortNameByDESC")
    public ResponseEntity<List<EmployeeResponseDto>> sortNameByDESC(@RequestParam Long departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findAllOrderedByFullNameDesc(departmentId));
    }

    @GetMapping(path = "/filterDateAndSalary")
    public ResponseEntity<List<EmployeeResponseDto>> filterByDateAndSalary(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam Double minSalary){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findNewHiresWithSalary(startDate,endDate,minSalary));
    }

    @GetMapping(path = "getEmployeePositionStats")
    public ResponseEntity<List<PositionStatsDTO>> getEmployeePositionStats(){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeePositionStats());
    }

    @GetMapping(path = "findUnpaidEmployees")
    public ResponseEntity<List<EmployeeResponseDto>> findUnpaid(@RequestParam PaymentMonth month, @RequestParam int year){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findUnpaidEmployees(month,year));
    }
}
