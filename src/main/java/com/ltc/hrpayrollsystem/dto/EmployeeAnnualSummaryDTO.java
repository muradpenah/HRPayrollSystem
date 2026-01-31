package com.ltc.hrpayrollsystem.dto;

import lombok.Data;

@Data
public class EmployeeAnnualSummaryDTO {
    private String fullName;
    private Double totalGross;
    private Double totalTax;
    private Double totalNet;

    public EmployeeAnnualSummaryDTO(String name,Number totalGross, Number totalTax, Number totalNet) {
        this.fullName = name;
        // Əgər null gələrsə 0.0 yazılsın (NullPointerException olmasın deyə)
        this.totalGross = (totalGross != null) ? totalGross.doubleValue() : 0.0;
        this.totalTax = (totalTax != null) ? totalTax.doubleValue() : 0.0;
        this.totalNet = (totalNet != null) ? totalNet.doubleValue() : 0.0;
    }
}
