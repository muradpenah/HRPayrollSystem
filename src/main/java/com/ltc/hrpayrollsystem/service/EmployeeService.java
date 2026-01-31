package com.ltc.hrpayrollsystem.service;

import com.ltc.hrpayrollsystem.dto.PositionStatsDTO;
import com.ltc.hrpayrollsystem.dto.request.EmployeeRequestDto;
import com.ltc.hrpayrollsystem.dto.response.EmployeeResponseDto;
import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    List<EmployeeResponseDto> searchByName(String keyword);
    List<EmployeeResponseDto> findByDepartmentAndSalaryRange( Long departmentId,
                                                              Double min,
                                                              Double max);
  //  double calculateTotalSalaryByDepartment(Long departmentId);
    List<EmployeeResponseDto> findTopEarners(Long departmentId);
    List<EmployeeResponseDto> findEmployeesWithNonCorporateEmails(Long departmentId);
    List<EmployeeResponseDto> findAllOrderByNetSalaryAsc(Long departmentId);
    List<EmployeeResponseDto> findAllOrderByNetSalaryDesc(Long departmentId);
    List<EmployeeResponseDto> findAllOrderedByFullNameAsc(Long departmentId);
    List<EmployeeResponseDto> findAllOrderedByFullNameDesc(Long departmentId);

    List<PositionStatsDTO> getEmployeePositionStats();
    List<EmployeeResponseDto> findNewHiresWithSalary(LocalDate startDate, LocalDate endDate, Double minSalary);
    List<EmployeeResponseDto> findUnpaidEmployees(PaymentMonth month, int year);
}
