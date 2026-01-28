package com.ltc.hrpayrollsystem.entity;

import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payrolls")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMonth paymentMonth;

    @Column(nullable = false)
    private double bonusAmount;

    @Column(nullable = false)
    private double taxPercentage=0.18;

    @Column(nullable = false)
    @Positive
    private double taxAmount;

    @Column(nullable = false)
    @Positive
    private double netSalary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
