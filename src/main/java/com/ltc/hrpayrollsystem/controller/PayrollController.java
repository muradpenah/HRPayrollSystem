package com.ltc.hrpayrollsystem.controller;

import com.ltc.hrpayrollsystem.dto.DepartmentMonthlySummaryDTO;
import com.ltc.hrpayrollsystem.dto.EmployeeAnnualSummaryDTO;
import com.ltc.hrpayrollsystem.dto.request.EmployeeRequestDto;
import com.ltc.hrpayrollsystem.dto.request.PayrollRequestDto;
import com.ltc.hrpayrollsystem.dto.response.EmployeeResponseDto;
import com.ltc.hrpayrollsystem.dto.response.PayrollResponseDto;
import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import com.ltc.hrpayrollsystem.service.impl.PayrollServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/payrolls")
@AllArgsConstructor
@Tag(name = "Payrolls Operation panel")
public class PayrollController {
    private final PayrollServiceImpl payrollService;
    @PostMapping(path = "/create")
    @Operation(summary = "Payrolls creations operation", description = " if couldn't find payroll , problem was handled by validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payroll created successfully"),
            @ApiResponse(responseCode = "404", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PayrollResponseDto> savePayroll(@RequestBody PayrollRequestDto payrollRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.savePayroll(payrollRequestDto));
    }

    @ApiResponse(responseCode = "204", description = "Payrolls fetched successfully")
    @Operation(summary = "Payrolls fetching operation", description = "if couldn't find payroll , problem was handled by validation")
    @GetMapping(path = "/getALl")
    public ResponseEntity<Page<PayrollResponseDto>> getAllPayrolls(@ParameterObject
                                                                     @PageableDefault(page = 0, size = 5, value = 10, sort = "id",
                                                                             direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<PayrollResponseDto> responseDtos = payrollService.getAllPayrolls(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping(path = "/get/{id}")
    @Operation(summary = "Payroll fetching operation by ID", description = "if couldn't find payroll , problem was handled by validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payroll fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Payroll not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PayrollResponseDto> getPayrollById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(payrollService.getPayrollById(id));
    }


    @PutMapping(path = "/update/{id}")
    @Operation(summary = "Payroll updating operation by ID ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payroll updated successfully"),
            @ApiResponse(responseCode = "404", description = "Payroll not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PayrollResponseDto> updateEmployee(@PathVariable Long id, @RequestBody PayrollRequestDto payrollRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(payrollService.updatePayroll(id, payrollRequestDto));
    }

    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Payroll deleting operation", description = "if couldn't find payroll , problem was handled by validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payroll deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payroll not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        payrollService.deletePayroll(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payroll deleted successfully by ID: " + id);
    }

    @GetMapping(path = "/getEmployee/{employeeId}")
    public ResponseEntity<List<PayrollResponseDto>> findByDepartmentId(@PathVariable Long employeeId) {
        return ResponseEntity.status(HttpStatus.OK).body(payrollService.findByEmployeeId(employeeId));
    }

    @GetMapping(path = "getByDepartmentAndDate")
    public ResponseEntity<List<PayrollResponseDto>> findByDepartmentAndDate(@RequestParam Long DepartmentId, @RequestParam PaymentMonth month, @RequestParam int year){
        return ResponseEntity.status(HttpStatus.OK).body(payrollService.findByDepartmentAndDate(DepartmentId,month,year));
    }

    @GetMapping(path = "getHistoryByEmployeeId")
    public ResponseEntity<List<PayrollResponseDto>> findByEmployeeId(@RequestParam Long employeeId){
        return ResponseEntity.status(HttpStatus.OK).body(payrollService.findByEmployeeId(employeeId));
    }

    @GetMapping(path = "getByEmployeeAndDate")
    public ResponseEntity<List<PayrollResponseDto>> findByEmployeeAndDate(@RequestParam Long employeeId, @RequestParam PaymentMonth month, @RequestParam int year){
        return ResponseEntity.status(HttpStatus.OK).body(payrollService.findByEmployeeAndDate(employeeId,month,year));
    }

    @GetMapping(path = "getDepartmentMonthlyStats")
    public ResponseEntity<DepartmentMonthlySummaryDTO> getDepartmentMonthlyStats(@RequestParam Long departmentId, @RequestParam PaymentMonth month, @RequestParam int year){
        return ResponseEntity.status(HttpStatus.OK).body(payrollService.getDepartmentMonthlyStats(departmentId,month,year));
    }

    @GetMapping(path = "getEmployeeAnnualStats")
    public ResponseEntity<EmployeeAnnualSummaryDTO> getEmployeeAnnualStats(@RequestParam Long employeeId, @RequestParam int year){
        return ResponseEntity.status(HttpStatus.OK).body(payrollService.getEmployeeAnnualStats(employeeId,year));
    }
}
