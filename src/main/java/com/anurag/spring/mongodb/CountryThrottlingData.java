package com.anurag.spring.mongodb;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "countrythrottling")
public class CountryThrottlingData {

	@Id
	private String country;
	
	private String startTime;

	private String endTime;

	private String amountLimit;

	private String currency;

	
	private String currencyPrecision;

	private String zoneId;

	private String defaultZoneId;
	
	private String businessDate;
	
	private String nextBusinessDate;
	
	private LimitBurstAlert limitBurstAlert;


	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAmountLimit() {
		return amountLimit;
	}

	public void setAmountLimit(String amountLimit) {
		this.amountLimit = amountLimit;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getDefaultZoneId() {
		return defaultZoneId;
	}

	public void setDefaultZoneId(String defaultZoneId) {
		this.defaultZoneId = defaultZoneId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCurrencyPrecision() {
		return currencyPrecision;
	}

	public void setCurrencyPrecision(String currencyPrecision) {
		this.currencyPrecision = currencyPrecision;
	}

	public LimitBurstAlert getLimitBurstAlert() {
		return limitBurstAlert;
	}

	public void setLimitBurstAlert(LimitBurstAlert limitBurstAlert) {
		this.limitBurstAlert = limitBurstAlert;
	}

	public String getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}

	public String getNextBusinessDate() {
		return nextBusinessDate;
	}

	public void setNextBusinessDate(String nextBusinessDate) {
		this.nextBusinessDate = nextBusinessDate;
	}
}
