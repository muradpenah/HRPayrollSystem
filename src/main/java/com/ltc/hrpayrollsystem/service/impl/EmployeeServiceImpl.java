package com.ltc.hrpayrollsystem.service.impl;

import com.ltc.hrpayrollsystem.dto.PositionStatsDTO;
import com.ltc.hrpayrollsystem.dto.request.EmployeeRequestDto;
import com.ltc.hrpayrollsystem.dto.response.EmployeeResponseDto;
import com.ltc.hrpayrollsystem.entity.Department;
import com.ltc.hrpayrollsystem.entity.Employee;
import com.ltc.hrpayrollsystem.enumaration.DepartmentStatus;
import com.ltc.hrpayrollsystem.enumaration.PaymentMonth;
import com.ltc.hrpayrollsystem.exception.DepartmentNotActiveException;
import com.ltc.hrpayrollsystem.exception.DepartmentNotFoundException;
import com.ltc.hrpayrollsystem.exception.EmployeeNotFoundException;
import com.ltc.hrpayrollsystem.repo.DepartmentRepo;
import com.ltc.hrpayrollsystem.repo.EmployeeRepo;
import com.ltc.hrpayrollsystem.repo.PayrollRepo;
import com.ltc.hrpayrollsystem.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final PayrollRepo payrollRepo;
    @Override
    public EmployeeResponseDto saveEmployee(EmployeeRequestDto employeeRequestDto) {
        Department department = departmentRepo.findById(employeeRequestDto.getDepartmentId()).orElseThrow(
                () -> new DepartmentNotFoundException("Department not found")
        );
        if (department.getStatus() != DepartmentStatus.ACTIVE) {
            throw new DepartmentNotActiveException("Error: Employee cannot be saved because the department is not active!");
        }
        Employee employee = new Employee();
        employee.setFullName(employeeRequestDto.getFullName());
        employee.setAddress(employeeRequestDto.getAddress());
        employee.setEmail(employeeRequestDto.getEmail());
        employee.setPhoneNumber(employeeRequestDto.getPhoneNumber());
        employee.setPosition(employeeRequestDto.getPosition());
        employee.setHireDate(employeeRequestDto.getHireDate());
        employee.setBaseSalary(employeeRequestDto.getBaseSalary());
        employee.setDepartment(department);
        Employee savedEmployee = employeeRepo.save(employee);
        return new EmployeeResponseDto(
            savedEmployee.getId(),
            savedEmployee.getFullName(),
            savedEmployee.getPosition(),
            savedEmployee.getHireDate(),
            savedEmployee.getBaseSalary(),
            savedEmployee.getEmail(),
            savedEmployee.getPhoneNumber(),
            savedEmployee.getAddress(),
            savedEmployee.getDepartment().getId()
        );
    }

    @Override
    public Page<EmployeeResponseDto> getAllEmployees(Pageable pageable) {
        return employeeRepo.findAll(pageable).map(employee -> new EmployeeResponseDto(
                employee.getId(),
                employee.getFullName(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getBaseSalary(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getAddress(),
                employee.getDepartment() != null ? employee.getDepartment().getId() : null
                ));
    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee employee = employeeRepo.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException("Employee not found")
        );
        return new EmployeeResponseDto(
                employee.getId(),
                employee.getFullName(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getBaseSalary(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getAddress(),
                employee.getDepartment().getId());
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeRepo.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException("Employee not found")
        );
        Department department = employee.getDepartment();
        if (department.getStatus() != DepartmentStatus.ACTIVE) {
            throw new DepartmentNotActiveException("Error: Employee cannot be updated because the department is not active!");
        }
        employee.setFullName(employeeRequestDto.getFullName());
        employee.setAddress(employeeRequestDto.getAddress());
        employee.setEmail(employeeRequestDto.getEmail());
        employee.setPhoneNumber(employeeRequestDto.getPhoneNumber());
        employee.setPosition(employeeRequestDto.getPosition());
        employee.setHireDate(employeeRequestDto.getHireDate());
        employee.setBaseSalary(employeeRequestDto.getBaseSalary());
        Employee savedEmployee = employeeRepo.save(employee);
        return new EmployeeResponseDto(
                employee.getId(),
                employee.getFullName(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getBaseSalary(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getAddress(),
                employee.getDepartment().getId());
    }

    @Override
    public void deleteEmployee(Long id) {
        if(!employeeRepo.existsById(id)){
            throw new EmployeeNotFoundException("Employee not found" + id);
        }
        employeeRepo.deleteById(id);
    }

    @Override
    public List<EmployeeResponseDto> findByDepartmentId(Long departmentId) {
        List<Employee> employees = employeeRepo.findByDepartmentId(departmentId);
        if(employees.isEmpty()){
            throw new DepartmentNotFoundException("Department has no employees " + departmentId);
        }
        return employees.stream().map(employee ->
                new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }

    @Override
    public List<EmployeeResponseDto> searchByName(String keyword) {
        return employeeRepo.searchByName(keyword).stream()
                .map(employee ->
                        new EmployeeResponseDto(
                                employee.getId(),
                                employee.getFullName(),
                                employee.getPosition(),
                                employee.getHireDate(),
                                employee.getBaseSalary(),
                                employee.getEmail(),
                                employee.getPhoneNumber(),
                                employee.getAddress(),
                                employee.getDepartment().getId())).toList();
    }

    @Override
    public List<EmployeeResponseDto> findByDepartmentAndSalaryRange(Long departmentId, Double min, Double max) {
        List<Employee> employees = employeeRepo.findByDepartmentId(departmentId);
        if(employees.isEmpty()){
            throw new DepartmentNotFoundException("Department has no employees " + departmentId);
        }
        return employeeRepo.findByDepartmentAndSalaryRange(departmentId,min,max).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }

   @Override
    public List<EmployeeResponseDto> findTopEarners(Long departmentId) {
       List<Employee> employees = employeeRepo.findByDepartmentId(departmentId);
       if(employees.isEmpty()){
           throw new DepartmentNotFoundException("Department has no employees" + departmentId);
       }
        return employeeRepo.findTopEarnersByDepartment(departmentId).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();

    }

    @Override
    public List<EmployeeResponseDto> findEmployeesWithNonCorporateEmails(Long departmentId) {
        List<Employee> employees = employeeRepo.findByDepartmentId(departmentId);
        if(employees.isEmpty()){
            throw new DepartmentNotFoundException("Department has no employees" + departmentId);
        }
        return employeeRepo.findEmployeesWithNonCorporateEmails(departmentId).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }

    @Override
    public List<EmployeeResponseDto> findAllOrderByNetSalaryAsc(Long departmentId) {
        List<Employee> employees = employeeRepo.findByDepartmentId(departmentId);
        if(employees.isEmpty()){
            throw new DepartmentNotFoundException("Department has no employees" + departmentId);
        }
        return employeeRepo.findAllOrderByNetSalaryAsc(departmentId).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }

    @Override
    public List<EmployeeResponseDto> findAllOrderByNetSalaryDesc(Long departmentId) {
        List<Employee> employees = employeeRepo.findByDepartmentId(departmentId);
        if(employees.isEmpty()){
            throw new DepartmentNotFoundException("Department has no employees" + departmentId);
        }
        return employeeRepo.findAllOrderByNetSalaryDesc(departmentId).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }

    @Override
    public List<EmployeeResponseDto> findAllOrderedByFullNameAsc(Long departmentId) {
        List<Employee> employees = employeeRepo.findByDepartmentId(departmentId);
        if(employees.isEmpty()){
            throw new DepartmentNotFoundException("Department has no employees" + departmentId);
        }
        return employeeRepo.findAllOrderedByFullNameAsc(departmentId).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }

    @Override
    public List<EmployeeResponseDto> findAllOrderedByFullNameDesc(Long departmentId) {
        List<Employee> employees = employeeRepo.findByDepartmentId(departmentId);
        if(employees.isEmpty()){
            throw new DepartmentNotFoundException("Department has no employees" + departmentId);
        }
        return employeeRepo.findAllOrderedByFullNameDesc(departmentId).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }

    @Override
    public List<EmployeeResponseDto> findNewHiresWithSalary(LocalDate startDate, LocalDate endDate, Double minSalary) {
        return employeeRepo.findNewHiresWithSalary(startDate,endDate,minSalary).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }

    @Override
    public List<PositionStatsDTO> getEmployeePositionStats() {
        return employeeRepo.getStatsByPosition().stream().map(
                dto -> new PositionStatsDTO(
                        dto.getPosition(),
                        dto.getEmployeeCount(),
                        dto.getTotalBaseSalary()
                        )).toList();
    }

    @Override
    public List<EmployeeResponseDto> findUnpaidEmployees(PaymentMonth month, int year) {
        return payrollRepo.findUnpaidEmployees(month,year).stream().map(
                employee -> new EmployeeResponseDto(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getBaseSalary(),
                        employee.getEmail(),
                        employee.getPhoneNumber(),
                        employee.getAddress(),
                        employee.getDepartment().getId())).toList();
    }
}
