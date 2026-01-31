package com.ltc.hrpayrollsystem.repo;

import com.ltc.hrpayrollsystem.dto.PositionStatsDTO;
import com.ltc.hrpayrollsystem.entity.Employee;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepo extends JpaRepository<Employee,Long> {
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId")
    List<Employee> findByDepartmentId(@Param("departmentId") Long departmentId);
    //Her department de isci sayi
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    long countByDepartmentId(@Param("departmentId") Long departmentId);
    // Adında və ya soyadında 'keyword' olan işçiləri tapmaq
    @Query("SELECT e FROM Employee e WHERE LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> searchByName(@Param("keyword") String keyword);
    // Müəyyən departamentdə və müəyyən maaş aralığında olan işçilər
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId AND e.baseSalary BETWEEN :minSalary AND :maxSalary")
    List<Employee> findByDepartmentAndSalaryRange(@Param("departmentId") Long deptId,
                                                  @Param("minSalary") Double min,
                                                  @Param("maxSalary") Double max);
    // Departamentin ümumi maaş büdcəsi
    @Query("SELECT SUM(e.baseSalary) FROM Employee e WHERE e.department.id = :departmentId")
    Double calculateTotalPayrollByDepartment(@Param("departmentId") Long departmentId);

    // Şirkət üzrə ən yüksək maaş alan işçi (Subquery istifadəsi)
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId AND e.baseSalary = (SELECT MAX(emp.baseSalary) FROM Employee emp WHERE emp.department.id = :departmentId )")
    List<Employee> findTopEarnersByDepartment(@Param("departmentId") Long departmentId);

    // Korporativ olmayan emailləri tapmaq
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId  AND e.email  NOT  LIKE '%@ltc.com'")
    List<Employee> findEmployeesWithNonCorporateEmails(@Param("departmentId") Long departmentId);

    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId ORDER BY e.baseSalary  ASC")
    List<Employee> findAllOrderByNetSalaryAsc(@Param("departmentId") Long departmentId);

    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId ORDER BY e.baseSalary  DESC")
    List<Employee> findAllOrderByNetSalaryDesc(@Param("departmentId") Long departmentId);

    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId ORDER BY e.fullName ASC")
    List<Employee> findAllOrderedByFullNameAsc(@Param("departmentId") Long departmentId);

    // 2. Full Name (Ad + Soyad) Azalan sıra (Z -> A)
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId ORDER BY  e.fullName  DESC")
    List<Employee> findAllOrderedByFullNameDesc(@Param("departmentId") Long departmentId);

    // GROUP BY istifadəsi: Vəzifələrə görə qruplaşdırıb statistika çıxarır
    @Query("SELECT new com.ltc.hrpayrollsystem.dto.PositionStatsDTO(e.position, COUNT(e), SUM(e.baseSalary)) FROM Employee e GROUP BY e.position")
    List<PositionStatsDTO> getStatsByPosition();

    // Tarix aralığı + Maaş şərti
    @Query("SELECT e FROM Employee e WHERE e.hireDate BETWEEN :startDate AND :endDate AND e.baseSalary >= :minSalary ORDER BY e.hireDate DESC")
    List<Employee> findNewHiresWithSalary(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("minSalary") Double minSalary);
}
