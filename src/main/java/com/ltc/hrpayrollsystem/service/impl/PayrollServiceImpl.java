package com.ltc.hrpayrollsystem.service.impl;

import com.ltc.hrpayrollsystem.dto.request.PayrollRequestDto;
import com.ltc.hrpayrollsystem.dto.response.PayrollResponseDto;
import com.ltc.hrpayrollsystem.repo.PayrollRepo;
import com.ltc.hrpayrollsystem.service.PayrollService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PayrollServiceImpl implements PayrollService {
    private final PayrollRepo payrollRepo;

    @Override
    public PayrollResponseDto savePayroll(PayrollRequestDto payrollRequestDto) {
        return null;
    }

    @Override
    public Page<PayrollResponseDto> getAllPayrolls(Pageable pageable) {
        return null;
    }

    @Override
    public PayrollResponseDto getPayrollById(Long id) {
        return null;
    }

    @Override
    public PayrollResponseDto updatePayroll(Long id, PayrollRequestDto payrollRequestDto) {
        return null;
    }

    @Override
    public void deletePayroll(Long id) {

    }

    @Override
    public List<PayrollResponseDto> findByEmployeeId(Long employeeId) {
        return List.of();
    }
}
