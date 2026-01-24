package com.ltc.hrpayrollsystem.service;

import com.ltc.hrpayrollsystem.dto.request.PayrollRequestDto;
import com.ltc.hrpayrollsystem.dto.response.PayrollResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PayrollService {
    //C
    PayrollResponseDto savePayroll(PayrollRequestDto payrollRequestDto);
    //R
    Page<PayrollResponseDto> getAllPayrolls(Pageable pageable);
    PayrollResponseDto getPayrollById(Long id);
    //U
    PayrollResponseDto updatePayroll(Long id, PayrollRequestDto payrollRequestDto);
    //D
    void deletePayroll(Long id);
    //Query
    List<PayrollResponseDto> findByEmployeeId(Long employeeId);
}
