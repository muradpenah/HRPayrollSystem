package com.ltc.hrpayrollsystem.dto.response;

import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollResponseDto {
    private Long id;
    private LocalDate paymentDate;
    private PaymentMonth paymentMonth;
    private double bonusAmount;
    private double taxPercentage;
    private double taxAmount;
    private double netSalary;
    private Long employeeId;
}
