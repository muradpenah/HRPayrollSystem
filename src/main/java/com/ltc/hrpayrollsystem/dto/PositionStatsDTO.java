package com.ltc.hrpayrollsystem.dto;

import lombok.Data;

@Data
public class PositionStatsDTO {

    private String position;
    private Long employeeCount;
    private Double totalBaseSalary;

    public PositionStatsDTO(String position, Long employeeCount, Number total) {
        this.position = position;
        this.employeeCount = employeeCount;
        this.totalBaseSalary = (total != null)? total.doubleValue() : 0.0;
    }
}
