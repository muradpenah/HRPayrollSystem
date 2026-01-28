package com.ltc.hrpayrollsystem.exception;

public class PayrollNotFoundException extends RuntimeException {
    public PayrollNotFoundException(String message) {
        super(message);
    }
}
