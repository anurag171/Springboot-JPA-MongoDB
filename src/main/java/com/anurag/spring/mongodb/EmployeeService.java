package com.anurag.spring.mongodb;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
	
	public void createEmployee(List<Employee> employees);
	public Collection<Employee> getAllEmployee();
    public Optional<Employee> findEmployeeById(String id);
    public void deleteEmployeeById(String id) ;
    public void updateEmployee(Employee employee);
    public void deleteAllEmployees();
}
