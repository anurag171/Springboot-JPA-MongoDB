package com.anurag.spring.mongodb;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	EmployeeRepository repositories;	

	@Override
	public Optional<Employee> findEmployeeById(String id) {
		return repositories.findById(id);
	}

	@Override
	public void deleteEmployeeById(String id) {
		repositories.deleteById(id);
	}

	

	@Override
	public void deleteAllEmployees() {
		repositories.deleteAll();
	}

	@Override
	public void createEmployee(List<Employee> employees) {
		repositories.saveAll(employees);
		
	}

	@Override
	public Collection<Employee> getAllEmployee() {
		return repositories.findAll();
	}

	@Override
	public void updateEmployee(Employee employee) {
		repositories.save(employee);		
	}
}