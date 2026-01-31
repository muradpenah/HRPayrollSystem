package com.ltc.hrpayrollsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DepartmentMonthlySummaryDTO {
    private String departmentName;
    private Long employeeCount;      // Maaş alan işçi sayı
    private Double totalGrossPay;    // Cəmi hesablanan (Gross)
    private Double totalNetPay;      // Cəmi ödənilən (Net)
    private Double totalTaxPaid;     // Cəmi vergi

    public DepartmentMonthlySummaryDTO(String departmentName, Long count,
                                       Number gross, Number net, Number tax) {
        this.departmentName = departmentName;
        this.employeeCount = count;
        // Gələn dəyəri Double-a çeviririk
        this.totalGrossPay = (gross != null) ? gross.doubleValue() : 0.0;
        this.totalNetPay = (net != null) ? net.doubleValue() : 0.0;
        this.totalTaxPaid = (tax != null) ? tax.doubleValue() : 0.0;
    }
}
