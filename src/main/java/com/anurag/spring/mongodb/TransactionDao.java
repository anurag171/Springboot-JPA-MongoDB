package com.anurag.spring.mongodb;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.anurag.spring.mongodb.staging.CountryStagingData;

public interface TransactionDao {
		
	void flushConfigurationOnStartUp();

		void updateOrInsert(CountryThrottlingData countrystaticData, String country, ZonedDateTime zonedDateTime,
				BigDecimal messgaeAmountBd);
		
		BigDecimal getAvailableDayLimit(CountryThrottlingData countrystaticData,ZonedDateTime zonedDateTime);

		int getNextAvailableDayAfterToday(CountryThrottlingData countrystaticData, ZonedDateTime date, BigDecimal messgaeAmountBd, int offset);
		
		boolean archivePayments(ArchivedPayments archivepayments);
		
		void getArchivePayments(String country,CountryThrottlingData countryData, Integer fetchSize);
		
		void auditDailyTransactionsLimit(String country, ZonedDateTime zonedDateTime,
				BigDecimal messgaeAmountBd,String sign);
		
		boolean persistDailyTransactions(CountryStagingData messageData);
}