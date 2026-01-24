package com.ltc.hrpayrollsystem.entity;

import com.ltc.hrpayrollsystem.enumaration.DepartmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String departmentName;

    @Column(nullable = false)
    private String departmentAddress;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DepartmentStatus status= DepartmentStatus.ACTIVE;

    @OneToMany(mappedBy = "department",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Employee> employees;

}
