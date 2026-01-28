package com.ltc.hrpayrollsystem.repo;

import com.ltc.hrpayrollsystem.entity.Payroll;
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
}
