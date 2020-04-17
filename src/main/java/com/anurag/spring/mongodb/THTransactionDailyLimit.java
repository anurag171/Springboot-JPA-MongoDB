package com.anurag.spring.mongodb;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@TypeAlias(value = "THLimit_")
@Document(collection = "thtxndailylimit")
@CompoundIndex(def = "{'country': 1, 'date': 1}")
public class THTransactionDailyLimit extends CountryTransactionDailyLimit {
	
	/*
	 * private String country;
	 * 
	 * public String getCountry() { return country; }
	 * 
	 * public void setCountry(String country) { this.country = country; }
	 */
}
