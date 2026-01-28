package com.ltc.hrpayrollsystem.exception;

public class DepartmentNotActiveException extends RuntimeException {
    public DepartmentNotActiveException(String message) {
        super(message);
    }
}
