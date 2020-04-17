package com.anurag.spring.mongodb;

import lombok.Data;

@Data
public class LimitBurstAlert {
	
	private String[] limitBurstPercentage;
	
	public String[] getLimitBurstPercentage() {
		return limitBurstPercentage;
	}

	public void setLimitBurstPercentage(String[] limitBurstPercentage) {
		this.limitBurstPercentage = limitBurstPercentage;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	private String toEmail;
	
	private String fromEmail;
	
	private String subject;

}
