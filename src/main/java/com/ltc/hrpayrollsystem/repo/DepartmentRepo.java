package com.ltc.hrpayrollsystem.repo;

import com.ltc.hrpayrollsystem.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo  extends JpaRepository<Department,Long> {
}
