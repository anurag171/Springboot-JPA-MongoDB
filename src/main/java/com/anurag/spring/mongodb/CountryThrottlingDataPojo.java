package com.anurag.spring.mongodb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryThrottlingDataPojo implements PropertyChangeListener{

	/**
	 * 
	 */

	Logger logger = LoggerFactory.getLogger(CountryThrottlingDataPojo.class);

	private String country;

	private String startTime;

	private String endTime;

	private String amountLimit;

	private String currency;

	private String currencyPrecision;

	private String zoneId;

	private String defaultZoneId;

	PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

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
		changeSupport.firePropertyChange("amountLimit", this.amountLimit, amountLimit);
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



	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener); }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		changeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		
	}





}
