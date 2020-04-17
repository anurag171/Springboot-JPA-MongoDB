package com.anurag.spring.mongodb;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

@Repository
public class TransactionLimitImplDao implements TransactionDao {
	
	Logger logger = LoggerFactory.getLogger(TransactionLimitImplDao.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	ApplicationContext _context;
	
	@Autowired
	AmountValidator validator;

	@Override
	public void updateOrInsert(CountryThrottlingData countryStaticData,String country, ZonedDateTime date,BigDecimal amount) {
		Query query = new Query();
		query.addCriteria(Criteria.where("country").is(country));
		query.addCriteria(Criteria.where("date").is(date.toLocalDate().toString()));
		CountryTransactionDailyLimit record = mongoTemplate.findOne(query, CountryDbClasses.getClazz(countryStaticData.getCountry()));
		if(record != null) {
			BigDecimal bd1 = new BigDecimal(record.getDailyLimit());
			bd1.setScale(Integer.parseInt(countryStaticData.getCurrencyPrecision()),RoundingMode.HALF_DOWN);
			BigDecimal newlimit =  bd1.add(amount, new MathContext(Integer.parseInt(countryStaticData.getCurrencyPrecision())));
			String amountStr = newlimit.toPlainString();
			record.setDailyLimit(amountStr);
			validator.validate(record, null);
		}else {
			record = getInstance(country, record);
			record.setCountry(country);
			record.setDailyLimit(amount.toPlainString());
			record.setDate(date.toLocalDate().toString());
			record.setDayOfYear(String.valueOf(date.getDayOfYear()));
			record.setMaxLimit(countryStaticData.getAmountLimit());
		}
		mongoTemplate.save(record);
		
	}

	private CountryTransactionDailyLimit getInstance(String country, CountryTransactionDailyLimit record) {
		switch(country) {
		case "SG":record = new SGTransactionDailyLimit();break;
		case "TH":record = new THTransactionDailyLimit();break;
		case "ID":record = new IDTransactionDailyLimit();break;
		};
		return record;
	}	
	
	@Override
	public BigDecimal getAvailableDayLimit(CountryThrottlingData countryStaticData,ZonedDateTime date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("country").is(countryStaticData.getCountry()));
		query.addCriteria(Criteria.where("date").is(date.toLocalDate().toString()));
		CountryTransactionDailyLimit record = mongoTemplate.findOne(query, CountryDbClasses.getClazz(countryStaticData.getCountry()));
		BigDecimal bd1 = null;
		if(null != record) {
			bd1 = new BigDecimal(record.getDailyLimit());
			bd1.setScale(Integer.parseInt(countryStaticData.getCurrencyPrecision()),RoundingMode.HALF_DOWN);		
		}else {
			bd1 = BigDecimal.ZERO;
		}
		return bd1;
	}

	@Override
	public int getNextAvailableDayAfterToday(CountryThrottlingData countrystaticData, ZonedDateTime date,BigDecimal amount,int offset) {
		logger.info(countrystaticData.getCountry() );
		int localOffset = offset;		
		ZonedDateTime newdate = date.plusDays(localOffset);
		BigDecimal limitOfDay = getAvailableDayLimit(countrystaticData,newdate);
		BigDecimal messgaeAmountBd1 = amount.add(limitOfDay, new MathContext(Integer.parseInt(countrystaticData.getCurrencyPrecision())) );
		messgaeAmountBd1.setScale(Integer.parseInt(countrystaticData.getCurrencyPrecision()),RoundingMode.HALF_DOWN);
		BigDecimal limitAmountBd = new BigDecimal(countrystaticData.getAmountLimit());
		limitAmountBd.setScale(Integer.parseInt(countrystaticData.getCurrencyPrecision()),RoundingMode.HALF_DOWN);
		if(limitAmountBd.compareTo(messgaeAmountBd1) >=0) {
			return localOffset;			
		}else {
			++localOffset;
			return getNextAvailableDayAfterToday(countrystaticData, date,amount,localOffset);
		}		
	}

	@Override
	public boolean archivePayments(ArchivedPayments archivepayments) {
		
		ArchivedPayments archivepaymentsMongo = mongoTemplate.save(archivepayments);
		
		return (null != archivepaymentsMongo ?  true: false);
	}

	@Override
	public void getArchivePayments(String country,CountryThrottlingData countryData) {
		ZoneId zoneId = ZoneId.of(countryData.getZoneId());
		Query query = new Query();
		query.addCriteria(Criteria.where("country").is(country));
		query.addCriteria(Criteria.where("dayOfYear").is(String.valueOf(ZonedDateTime.now(zoneId).getDayOfYear()+1)));
		Update update = new Update();
		update.set("processed","1");
		update.filterArray(Criteria.where("country").is(country).andOperator(Criteria.where("dayOfYear").is(String.valueOf(ZonedDateTime.now(zoneId).getDayOfYear()+1))));
		List<ArchivedPayments> list = mongoTemplate.find(query, ArchivedPayments.class);
		if(list.size()>0) {
			JmsTemplate _jmsTemplate =  _context.getBean(JmsTemplate.class);
			list.forEach((k)->_jmsTemplate.convertAndSend("jms.message.endpoint",k.getMessage()));
			
		}
		UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ArchivedPayments.class);
		long count = updateResult.getModifiedCount();
		if(count>0) {
			logger.info("Rows update for [{}] = [{}]",country,count);
		}else {
			logger.error("No row updated for country [{}]",country);
		}
	}

	@Override
	public void flushConfigurationOnStartUp() {
		
		Map<String,String> map = CountryDbClasses.getCollection();
		
		Set<String> set = map.keySet();
		for(String country: set) {
			String collectionName = map.get(country); 
			logger.info("flushing the collection [{}]",collectionName);
			mongoTemplate.dropCollection(collectionName);			
		}		
	}
}
