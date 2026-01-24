package com.ltc.hrpayrollsystem.repo;

import com.ltc.hrpayrollsystem.entity.Payroll;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayrollRepo extends JpaRepository<Payroll,Long> {
    @Query("SELECT p FROM Payroll p WHERE p.employee.id = :employeeId")
    List<Payroll> findByEmployeeId(@Param("employeeId") Long employeeId);
}
