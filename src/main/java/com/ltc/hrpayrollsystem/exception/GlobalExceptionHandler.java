package com.ltc.hrpayrollsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatePayrollException.class)
    public ResponseEntity<String> duplicatePayrollHandler(DuplicatePayrollException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PayrollNotFoundException.class)
    public ResponseEntity<String> payrollNotFoundHandler(PayrollNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<String> employeeNotFoundHandler(EmployeeNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<String> departmentNotFoundHandler(DepartmentNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DepartmentNotActiveException.class)
    public ResponseEntity<String> departmentNotActiveHandler(DepartmentNotActiveException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EmployeeAlreadyExistDepartmentException.class)
    public ResponseEntity<String> employeeAlreadyNotExistDepartmentHandler(EmployeeAlreadyExistDepartmentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);}
}
