package com.anurag.spring.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Employee{
	
	@Id
	private String id;
	
	private String name;
	private String designation;
	private String location;
	private int age;
	private int retirementDue;
	private int retirementDueInYears;

	public Employee(String name,String designation,String location,int age,int retirementDue,int retirementDueInYears) {
		this.setName(name);
		this.setDesignation(designation);
		this.setAge(age);
		this.setLocation(location);
		this.setRetirementDue(retirementDue);
		this.setRetirementDueInYears(retirementDueInYears);
	}
	
	/*
	 * @Override public String toString() { return String.format(
	 * "Employee[id=%s, name='%s', designation='%s' , location='%s',age='%s',retirementDue='%s',retirementDueInYears='%s']"
	 * , id, getName(), getDesignation(),getLocation(),getAge(),getRetirementDue(),
	 * getRetirementDueInYears()); }
	 */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getRetirementDue() {
		return retirementDue;
	}

	public void setRetirementDue(int retirementDue) {
		this.retirementDue = retirementDue;
	}

	public int getRetirementDueInYears() {
		return retirementDueInYears;
	}

	public void setRetirementDueInYears(int retirementDueInYears) {
		this.retirementDueInYears = retirementDueInYears;
	}
	
	@Override
	public boolean equals(Object emp) {
		
		if(emp instanceof Employee) {
			Employee cast  = (Employee)emp;
			return this.id.equals(cast.getId());			
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Integer.parseInt(this.id);
	}

	

}
