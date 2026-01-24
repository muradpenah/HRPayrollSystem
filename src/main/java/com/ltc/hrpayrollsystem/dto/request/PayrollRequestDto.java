package com.ltc.hrpayrollsystem.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollRequestDto {
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;
    @NotNull
    private PaymentMonth paymentMonth;
    @NotNull
    @Positive
    private double bonusAmount;
    @NotNull
    @Positive
    private double taxAmount;
    @NotNull
    @Positive
    private double netSalary;
    private Long employeeId;
}
