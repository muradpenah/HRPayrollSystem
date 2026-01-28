package com.ltc.hrpayrollsystem.repo;

import com.ltc.hrpayrollsystem.entity.Employee;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepo extends JpaRepository<Employee,Long> {
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId")
    List<Employee> findByDepartmentId(@Param("departmentId") Long departmentId);
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    long countByDepartmentId(@Param("departmentId") Long departmentId);
}
