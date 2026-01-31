package com.ltc.hrpayrollsystem.service.impl;

import com.ltc.hrpayrollsystem.dto.DepartmentMonthlySummaryDTO;
import com.ltc.hrpayrollsystem.dto.EmployeeAnnualSummaryDTO;
import com.ltc.hrpayrollsystem.dto.request.PayrollRequestDto;
import com.ltc.hrpayrollsystem.dto.response.PayrollResponseDto;
import com.ltc.hrpayrollsystem.entity.Department;
import com.ltc.hrpayrollsystem.entity.Employee;
import com.ltc.hrpayrollsystem.entity.Payroll;
import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import com.ltc.hrpayrollsystem.exception.DepartmentNotFoundException;
import com.ltc.hrpayrollsystem.exception.DuplicatePayrollException;
import com.ltc.hrpayrollsystem.exception.EmployeeNotFoundException;
import com.ltc.hrpayrollsystem.exception.PayrollNotFoundException;
import com.ltc.hrpayrollsystem.repo.DepartmentRepo;
import com.ltc.hrpayrollsystem.repo.EmployeeRepo;
import com.ltc.hrpayrollsystem.repo.PayrollRepo;
import com.ltc.hrpayrollsystem.service.PayrollService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@AllArgsConstructor
public class PayrollServiceImpl implements PayrollService {
    private final PayrollRepo payrollRepo;
    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    @Override
    public PayrollResponseDto savePayroll(PayrollRequestDto payrollRequestDto) {
        Employee employee = employeeRepo.findById(payrollRequestDto.getEmployeeId()).orElseThrow(
                () -> new EmployeeNotFoundException("Employee not found")
        );
        LocalDate paymentDate = payrollRequestDto.getPaymentDate();
        LocalDate startDate = paymentDate.withDayOfMonth(1);
        LocalDate endDate = paymentDate.with(TemporalAdjusters.lastDayOfMonth());
        boolean exists = payrollRepo.checkPayrollExists(
                payrollRequestDto.getEmployeeId(),
                startDate,
                endDate
        );
        if (exists) {
            throw new DuplicatePayrollException("Xəta: Bu işçiyə " + paymentDate.getMonth() + " ayı üçün artıq maaş hesablanıb!");
        }

        Payroll payroll = new Payroll();
        payroll.setPaymentDate(payrollRequestDto.getPaymentDate());
        payroll.setPaymentMonth(PaymentMonth.valueOf(
                payrollRequestDto.getPaymentDate().getMonth().name()));
        payroll.setBonusAmount(payrollRequestDto.getBonusAmount());
        payroll.setTaxAmount(employee.getBaseSalary() * payroll.getTaxPercentage());
        payroll.setNetSalary(employee.getBaseSalary() + payroll.getBonusAmount() - payroll.getTaxAmount());
        payroll.setEmployee(payroll.getEmployee());
        Payroll savedPayroll = payrollRepo.save(payroll);
        return new PayrollResponseDto(
                savedPayroll.getId(),
                savedPayroll.getPaymentDate(),
                savedPayroll.getPaymentMonth(),
                savedPayroll.getBonusAmount(),
                savedPayroll.getTaxPercentage(),
                savedPayroll.getTaxAmount(),
                savedPayroll.getNetSalary(),
                savedPayroll.getEmployee().getId()
        );
    }

    @Override
    public Page<PayrollResponseDto> getAllPayrolls(Pageable pageable) {
        return payrollRepo.findAll(pageable).map(payroll -> new PayrollResponseDto(
                payroll.getId(),
                payroll.getPaymentDate(),
                payroll.getPaymentMonth(),
                payroll.getBonusAmount(),
                payroll.getTaxPercentage(),
                payroll.getTaxAmount(),
                payroll.getNetSalary(),
                payroll.getEmployee() != null ? payroll.getEmployee().getId() : null
        ));
    }

    @Override
    public PayrollResponseDto getPayrollById(Long id) {
        Payroll payroll = payrollRepo.findById(id).orElseThrow(
                ()-> new PayrollNotFoundException("Payroll not found " + id));
        return new PayrollResponseDto(
                payroll.getId(),
                payroll.getPaymentDate(),
                payroll.getPaymentMonth(),
                payroll.getBonusAmount(),
                payroll.getTaxPercentage(),
                payroll.getTaxAmount(),
                payroll.getNetSalary(),
                payroll.getEmployee().getId());
    }

    @Override
    public PayrollResponseDto updatePayroll(Long id, PayrollRequestDto payrollRequestDto) {
        Payroll payroll = payrollRepo.findById(id).orElseThrow(
                ()-> new PayrollNotFoundException("Payroll not found " + id)
        );
        Employee employee = employeeRepo.findById(payrollRequestDto.getEmployeeId()).orElseThrow(
                () -> new EmployeeNotFoundException("Employee not found")
        );
        LocalDate paymentDate = payrollRequestDto.getPaymentDate();
        LocalDate startDate = paymentDate.withDayOfMonth(1);
        LocalDate endDate = paymentDate.with(TemporalAdjusters.lastDayOfMonth());
        boolean exists = payrollRepo.checkPayrollExists(
                payrollRequestDto.getEmployeeId(),
                startDate,
                endDate
        );
        if (exists) {
            throw new DuplicatePayrollException("Xəta: Bu işçiyə " + paymentDate.getMonth() + " ayı üçün artıq maaş hesablanıb!");
        }
        payroll.setPaymentDate(payrollRequestDto.getPaymentDate());
        payroll.setPaymentMonth(PaymentMonth.valueOf(
                payrollRequestDto.getPaymentDate().getMonth().name()));
        payroll.setBonusAmount(payrollRequestDto.getBonusAmount());
        payroll.setNetSalary(employee.getBaseSalary() + payroll.getBonusAmount() - payroll.getTaxAmount());
        Payroll savedPayroll = payrollRepo.save(payroll);
        return new PayrollResponseDto(
                savedPayroll.getId(),
                savedPayroll.getPaymentDate(),
                savedPayroll.getPaymentMonth(),
                savedPayroll.getBonusAmount(),
                savedPayroll.getTaxPercentage(),
                savedPayroll.getTaxAmount(),
                savedPayroll.getNetSalary(),
                savedPayroll.getEmployee().getId()
        );
    }

    @Override
    public void deletePayroll(Long id) {
        if(!payrollRepo.existsById(id)){
            throw new PayrollNotFoundException("Payroll not found " + id);
        }
        payrollRepo.deleteById(id);
    }

    @Override
    public List<PayrollResponseDto> findByEmployeeId(Long employeeId) {
        List<Payroll> payrolls = payrollRepo.findByEmployeeId(employeeId);
        if(payrolls.isEmpty()){
            throw new EmployeeNotFoundException("Employee not found " + employeeId);
        }
        return payrolls.stream().map(payroll -> new PayrollResponseDto(
                payroll.getId(),
                payroll.getPaymentDate(),
                payroll.getPaymentMonth(),
                payroll.getBonusAmount(),
                payroll.getTaxPercentage(),
                payroll.getTaxAmount(),
                payroll.getNetSalary(),
                payroll.getEmployee().getId())).toList();
    }

    @Override
    public List<PayrollResponseDto> findByDepartmentAndDate(Long departmentId, PaymentMonth month, int year) {
        Department department = departmentRepo.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department not found " + departmentId)
        );
        return payrollRepo.findByDepartmentAndMonth(departmentId,month,year).stream().map(
                payroll -> new PayrollResponseDto(
                payroll.getId(),
                payroll.getPaymentDate(),
                payroll.getPaymentMonth(),
                payroll.getBonusAmount(),
                payroll.getTaxPercentage(),
                payroll.getTaxAmount(),
                payroll.getNetSalary(),
                payroll.getEmployee().getId())).toList();
    }

    @Override
    public List<PayrollResponseDto> findHistoryByEmployeeId(Long employeeId) {
        List<Payroll> payrolls = payrollRepo.findHistoryByEmployee(employeeId);
        if(payrolls.isEmpty()){
            throw new EmployeeNotFoundException("Employee not found " + employeeId);
        }
        return payrolls.stream().map(payroll -> new PayrollResponseDto(
                payroll.getId(),
                payroll.getPaymentDate(),
                payroll.getPaymentMonth(),
                payroll.getBonusAmount(),
                payroll.getTaxPercentage(),
                payroll.getTaxAmount(),
                payroll.getNetSalary(),
                payroll.getEmployee().getId())).toList();
    }

    @Override
    public List<PayrollResponseDto> findByEmployeeAndDate(Long employeeId, PaymentMonth month, int year) {
        List<Payroll> payrolls = payrollRepo.findByEmployeeAndMonth(employeeId,month,year);
        if(payrolls.isEmpty()){
            throw new EmployeeNotFoundException("Employee not found " + employeeId);
        }
        return payrolls.stream().map(payroll -> new PayrollResponseDto(
                payroll.getId(),
                payroll.getPaymentDate(),
                payroll.getPaymentMonth(),
                payroll.getBonusAmount(),
                payroll.getTaxPercentage(),
                payroll.getTaxAmount(),
                payroll.getNetSalary(),
                payroll.getEmployee().getId())).toList();
    }

    @Override
    public DepartmentMonthlySummaryDTO getDepartmentMonthlyStats(Long departmentId, PaymentMonth month, int year) {
        Department department = departmentRepo.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department not found " + departmentId)
        );
        return payrollRepo.getDepartmentMonthlyStats(departmentId,month,year);
    }

    @Override
    public EmployeeAnnualSummaryDTO getEmployeeAnnualStats(Long employeeId, int year) {
        Employee employee = employeeRepo.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee not found " + employeeId)
        );
        return payrollRepo.getEmployeeAnnualStats(employeeId,year);
    }
}
