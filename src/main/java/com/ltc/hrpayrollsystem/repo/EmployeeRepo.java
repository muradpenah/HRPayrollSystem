package com.ltc.hrpayrollsystem.repo;

import com.ltc.hrpayrollsystem.entity.Employee;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee,Long> {
}
