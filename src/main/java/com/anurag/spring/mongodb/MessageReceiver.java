package com.anurag.spring.mongodb;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {
	
	private final static Logger logger = LoggerFactory.getLogger(ServiceUtil.class);
	
	@Autowired
	MessageAcceptService _messageService;
	
	@Autowired
	CountryThrottlingRepo repository;
	
	@Autowired
	Environment env;
	
	@Autowired
	TransactionDao transactionDao;
	
	@Autowired
	ServiceUtil serviceUtil;
	
	@Autowired
	ApplicationContext context;
	
	
	@JmsListener(destination = "jms.message.endpoint")
    public void receiveMessage(Message msg) 
    {
		@SuppressWarnings("unchecked")
		Map<String,CountryThrottlingData> dataMap = 	  context.getBean("throttlingBean",Map.class);
		//Optional<CountryThrottlingData> data = repository.findById(msg.getCountry());
		//CountryThrottlingData data =  dataMap.get(msg.getCountry());
		if(null != dataMap && !dataMap.isEmpty() ) {
			logger.info("received message [{}]",msg.toString());
			CountryThrottlingData countrystaticData = dataMap.get(msg.getCountry());
			serviceUtil.persistDailyTransactions(msg,countrystaticData,transactionDao);
			/*if(null != countrystaticData) {
				System.out.println(serviceUtil.getCountryTimeNow(countrystaticData));
				System.out.println("Received " + msg );
				ZonedDateTime paymentTime = serviceUtil.getCountryTimeOf(msg.getDate(),countrystaticData);
				System.out.println("msg date in singapore -->" + serviceUtil.getCountryTimeOf(msg.getDate(),countrystaticData));
				boolean isSameday = serviceUtil.isSameDay(paymentTime,countrystaticData);
				System.out.println("isSameDay " +  isSameday);
				boolean isWithinTimeLimit = serviceUtil.isWithInTimeZoneLimit(paymentTime,countrystaticData);
				System.out.println("isWithinTimeLimit " +  isWithinTimeLimit);
				if(isSameday && isWithinTimeLimit) {
					serviceUtil.checkAndUpdateLimit(msg, countrystaticData,transactionDao);
				}else {
					ZonedDateTime newdate = paymentTime.plusDays(1);
					serviceUtil.archivePayments(msg, transactionDao,newdate);
				}
			}else {
				System.err.println("data not present for country" + msg.getCountry());
			}*/
		}else {
			System.err.println("Nothing there");
		}
        _messageService.save(msg);
    }
}
