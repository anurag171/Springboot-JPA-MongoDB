package com.anurag.spring.mongodb;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class AmountValidator implements Validator {

	@Autowired
	ApplicationContext context;
	
	@Autowired
	MailClient mailClient;

	@Override
	public boolean supports(Class<?> clazz) {
		return CountryTransactionDailyLimit.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		synchronized (AmountValidator.class) {			
			if(target instanceof CountryTransactionDailyLimit) {
				CountryTransactionDailyLimit dailylimt =  (CountryTransactionDailyLimit) target;
				@SuppressWarnings("unchecked")
				Map<String,CountryThrottlingData> dataMap =  context.getBean("throttlingBean",Map.class);
				CountryThrottlingData data = dataMap.get(dailylimt.getCountry());
				String[] percentageArray = data.getLimitBurstAlert().getLimitBurstPercentage();
				String maxlimit = dailylimt.getMaxLimit();
				String dailyLimit = dailylimt.getDailyLimit();
				Double percentage = (Double.valueOf(dailyLimit)*100)/Double.valueOf(maxlimit);
				Double lowerlimit = Double.valueOf(percentageArray[0]);
				Double upperlimit = Double.valueOf(percentageArray[1]);
				Double maxLimit = Double.valueOf(100);
				if(percentage.compareTo(lowerlimit)>=0 && percentage.compareTo(upperlimit)<0 && !dailylimt.isLowerLimitFlag()) {
					mailClient.prepareAndSend(data.getLimitBurstAlert().getToEmail(), lowerlimit + "% Limit reached for " + data.getCountry(),data.getLimitBurstAlert().getFromEmail(),data.getLimitBurstAlert().getSubject());
					dailylimt.setLowerLimitFlag(true);
				}else if(percentage.compareTo(upperlimit)>=0 && percentage.compareTo(maxLimit)<0 && !dailylimt.isUpperLimitFlag()) {
					mailClient.prepareAndSend(data.getLimitBurstAlert().getToEmail(), upperlimit + "% Limit reached for " + data.getCountry(),data.getLimitBurstAlert().getFromEmail(),data.getLimitBurstAlert().getSubject());
					dailylimt.setUpperLimitFlag(true);
				}else {
					
				}
				
			}			
		}
	}

}



