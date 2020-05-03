package com.anurag.spring.mongodb;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.anurag.spring.mongodb.staging.CountryStagingData;

@Repository
public class TransactionImplDao implements TransactionDao {
	
	Logger logger = LoggerFactory.getLogger(TransactionImplDao.class);
	
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
	public void getArchivePayments(String country,CountryThrottlingData countryData,Integer fetchSize) {
		Consumer<CountryStagingData> updateStatus = (a) -> {
            a.setPicked(1);
            a.setProcessed(1);
            mongoTemplate.save(a);
        };
        
        logger.info("Processing for country [{}] for fetch size [{}]",country,fetchSize);
		
        Query query = new Query();
		query.addCriteria(Criteria.where("country").is("SG"));
		query.addCriteria(Criteria.where("processed").is(Integer.valueOf(0)));
		Pageable p = PageRequest.of(0, fetchSize, Sort.by(Direction.ASC, "receivedDateTime"));
		query.with(p);
		
		List<CountryStagingData> list = mongoTemplate.find(query, CountryDbClasses.getStagingClazz(country));
		/*BulkOperations bops = mongoTemplate.bulkOps(BulkMode.UNORDERED, SGStagingData.class);
		bops.updateMulti(query, update).execute();*/
		list.forEach(dataObject->updateStatus.accept(dataObject));
	}

	@Override
	public void flushConfigurationOnStartUp() {
		
		Map<String,String> map = CountryDbClasses.getCollection();
		Map<String,String> stagingmap = CountryDbClasses.getStagingCollection();
		
		Set<String> set = map.keySet();
		for(String country: set) {
			String collectionName = map.get(country); 
			logger.info("flushing the collection [{}]",collectionName);
			mongoTemplate.dropCollection(collectionName);			
		}	
		
	    set = stagingmap.keySet();
		for(String country: set) {
			String collectionName = stagingmap.get(country); 
			logger.info("flushing the collection [{}]",collectionName);
			mongoTemplate.dropCollection(collectionName);			
		}	
	}
	
	/**
	 * 
	 */
	@Override
	public void auditDailyTransactionsLimit(String country, ZonedDateTime zonedDateTime, BigDecimal messgaeAmountBd,
			String sign) {
		
		
	}

	@Override
	public boolean persistDailyTransactions(CountryStagingData message) {
		CountryStagingData data = null;
		try {
			 data = mongoTemplate.save(message);
		}catch(Exception ex) {
			logger.error("Error while persisting message [{}]",message);
		}
		return (null == data?false:true);
	}
}
