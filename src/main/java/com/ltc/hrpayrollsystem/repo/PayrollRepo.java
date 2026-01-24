package com.ltc.hrpayrollsystem.repo;

import com.ltc.hrpayrollsystem.entity.Payroll;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

public interface PayrollRepo extends JpaRepository<Payroll,Long> {
}
