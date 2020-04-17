package com.anurag.spring.mongodb;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;

public abstract class CountryTransactionDailyLimit {
	@Id
	private BigInteger  id;
	
	private String maxLimit;
	private String dailyLimit;	
	private String date;	
	private String dayOfYear;
	private String country;
	private boolean lowerLimitFlag = false;
	private boolean upperLimitFlag = false;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getMaxLimit() {
		return maxLimit;
	}

	public void setMaxLimit(String maxLimit) {
		this.maxLimit = maxLimit;
	}

	public String getDailyLimit() {
		return dailyLimit;
	}

	public void setDailyLimit(String dailyLimit) {
		this.dailyLimit = dailyLimit;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDayOfYear() {
		return dayOfYear;
	}

	public void setDayOfYear(String dayOfYear) {
		this.dayOfYear = dayOfYear;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger  id) {
		this.id = id;
	}

	public boolean isLowerLimitFlag() {
		return lowerLimitFlag;
	}

	public void setLowerLimitFlag(boolean lowerLimitFlag) {
		this.lowerLimitFlag = lowerLimitFlag;
	}

	public boolean isUpperLimitFlag() {
		return upperLimitFlag;
	}

	public void setUpperLimitFlag(boolean upperLimitFlag) {
		this.upperLimitFlag = upperLimitFlag;
	}


}
