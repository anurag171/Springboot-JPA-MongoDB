package com.anurag.spring.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

@Controller
@RequestMapping("/api/user/")
public class EmployeeController {
	
	@Autowired
	EmployeeService _service;

	@PostMapping(value = "/create")
	public String create(@RequestBody List<Employee> employee) {
		_service.createEmployee(employee);
		return "employee created";
	}
	
	@GetMapping(value = "/getall")
	public String getAll(){
		Gson gson = new Gson();		
		String x = gson.toJson(_service.getAllEmployee());		
		System.out.println(x);
		return x;
	}
	
	@GetMapping(value= "/getbyid/{employee-id}")
    public String getById(@PathVariable(value= "employee-id") String id) {
		Gson gson = new Gson();
		String x = gson.toJson(_service.findEmployeeById(id));
		System.out.println(x);
		return x;
    }
	
	@PutMapping(value= "/update/{employee-id}")
    public String update(@PathVariable(value= "employee-id") String id, @RequestBody Employee e) {
        e.setId(id);
        _service.updateEmployee(e);
        return "Employee record for employee-id= " + id + " updated.";
    }
	
	/**
     * Method to delete employee by id.
     * @param id
     * @return
     */
    @DeleteMapping(value= "/delete/{employee-id}")
    public String delete(@PathVariable(value= "employee-id") String id) {
    	_service.deleteEmployeeById(id);
        return "Employee record for employee-id= " + id + " deleted.";
    }
 
    /**
     * Method to delete all employees from the db.
     * @return
     */
    @DeleteMapping(value= "/deleteall")
    public String deleteAll() {
        _service.deleteAllEmployees();
        return "All employee records deleted.";
    }
	

}
