package com.ltc.hrpayrollsystem.repo;

import com.ltc.hrpayrollsystem.dto.DepartmentMonthlySummaryDTO;
import com.ltc.hrpayrollsystem.dto.EmployeeAnnualSummaryDTO;
import com.ltc.hrpayrollsystem.entity.Employee;
import com.ltc.hrpayrollsystem.entity.Payroll;
import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PayrollRepo extends JpaRepository<Payroll,Long> {
    @Query("SELECT p FROM Payroll p WHERE p.employee.id = :employeeId")
    List<Payroll> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(p) > 0 FROM Payroll p WHERE p.employee.id = :employeeId AND p.paymentDate BETWEEN :start AND :end")
    boolean checkPayrollExists(@Param("employeeId") Long employeeId,
                               @Param("start") LocalDate startDate,
                               @Param("end") LocalDate endDate);

    @Query("SELECT p FROM Payroll p WHERE p.employee.id = :employeeId ORDER BY p.paymentDate DESC")
    List<Payroll> findHistoryByEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT p FROM Payroll p  WHERE p.employee.department.id = :departmentId AND p.paymentMonth = :month  AND YEAR(p.paymentDate) = :year")
    List<Payroll> findByDepartmentAndMonth(@Param("departmentId") Long departmentId,
                                           @Param("month") PaymentMonth month,
                                           @Param("year") int year);

    @Query("SELECT e FROM Employee e WHERE e.id NOT IN (SELECT p.employee.id FROM Payroll p WHERE p.paymentMonth = :month AND YEAR(p.paymentDate) = :year)")
    List<Employee> findUnpaidEmployees(@Param("month") PaymentMonth month, @Param("year") int year);

    @Query("SELECT p FROM Payroll p WHERE p.employee.id = :employeeId AND p.paymentMonth = :month AND YEAR(p.paymentDate) = :year")
    List<Payroll> findByEmployeeAndMonth(@Param("employeeId") Long employeeId,
                                             @Param("month") PaymentMonth month,
                                             @Param("year") int year);

    @Query("SELECT new com.ltc.hrpayrollsystem.dto.DepartmentMonthlySummaryDTO(p.employee.department.departmentName, COUNT(p), SUM(p.employee.baseSalary + p.bonusAmount), SUM(p.netSalary), SUM(p.taxAmount) ) FROM Payroll p WHERE p.employee.department.id = :departmentId AND p.paymentMonth = :month AND YEAR(p.paymentDate) = :year GROUP BY p.employee.department.departmentName")
    DepartmentMonthlySummaryDTO getDepartmentMonthlyStats(@Param("departmentId") Long departmentId,
                                                          @Param("month") PaymentMonth month,
                                                          @Param("year") int year);

    @Query("SELECT new com.ltc.hrpayrollsystem.dto.EmployeeAnnualSummaryDTO(p.employee.fullName, SUM(p.employee.baseSalary + p.bonusAmount), SUM(p.taxAmount), SUM(p.netSalary)) FROM Payroll p WHERE p.employee.id = :employeeId AND YEAR(p.paymentDate) = :year GROUP BY p.employee.fullName")
    EmployeeAnnualSummaryDTO getEmployeeAnnualStats(@Param("employeeId") Long employeeId, @Param("year") int year);

}
