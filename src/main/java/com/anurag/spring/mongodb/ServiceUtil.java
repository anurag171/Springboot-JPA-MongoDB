package com.anurag.spring.mongodb;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.anurag.spring.mongodb.staging.CountryStagingData;

@Component
public class ServiceUtil {

	Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

	public   ZonedDateTime getCountryTimeNow(CountryThrottlingData countryData) {
		Instant nowUtc = Instant.now();
		ZoneId asiaSingapore = ZoneId.of(countryData.getZoneId());
		ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, asiaSingapore);
		return nowAsiaSingapore;
	}

	public   ZonedDateTime getCountryTimeOf(LocalDateTime date,CountryThrottlingData countryData) {
		ZoneId zoneIdDefault = ZoneId.of(countryData.getDefaultZoneId());
		ZoneId countryZoneId = ZoneId.of(countryData.getZoneId());
		logger.info("Country ZoneId " + countryZoneId +  " defaultZoneId " + zoneIdDefault);
		logger.info("Received date "+  date);
		ZonedDateTime defaultZone = date.atZone(zoneIdDefault);
		ZonedDateTime nowAsiaSingapore = defaultZone.withZoneSameInstant(countryZoneId);
		return nowAsiaSingapore;
	}

	public  boolean isSameDay(ZonedDateTime paymentTime, CountryThrottlingData countryData) {
		ZoneId zoneId = ZoneId.of(countryData.getZoneId());
		LocalDateTime currentimelocal = LocalDateTime.now();
		ZonedDateTime currentZone = currentimelocal.atZone(zoneId);
		Period diff = Period.between(paymentTime.toLocalDate(), currentZone.toLocalDate());
		if(0>=diff.getDays()) {
			return true;
		}else {
			return false;
		}		
	}

	public  boolean isWithInTimeZoneLimit(ZonedDateTime paymentTime, CountryThrottlingData countryData) {
		LocalTime starttime = LocalTime.parse(countryData.getStartTime());
		LocalTime endtime = LocalTime.parse(countryData.getEndTime());
		LocalTime messageLocalTime  = paymentTime.toLocalTime();
		logger.info("Local time of message " + messageLocalTime);

		return !messageLocalTime.isBefore(starttime) && !messageLocalTime.isAfter(endtime);
	}

	@Async
	public  synchronized CompletableFuture<Boolean> checkAndUpdateLimit(Message message, CountryThrottlingData countryData, TransactionDao limitRepo) {
		ZonedDateTime date = getCountryTimeOf(message.getDate(),countryData);
		BigDecimal currentDayLimit =  limitRepo.getAvailableDayLimit(countryData,date);
		BigDecimal messgaeAmountBd = new BigDecimal(message.getAmount());
		BigDecimal messgaeAmountBd1 = messgaeAmountBd.add(currentDayLimit, new MathContext(Integer.parseInt(countryData.getCurrencyPrecision())) );
		messgaeAmountBd1.setScale(Integer.parseInt(countryData.getCurrencyPrecision()),RoundingMode.HALF_DOWN);
		BigDecimal limitAmountBd = new BigDecimal(countryData.getAmountLimit());
		limitAmountBd.setScale(Integer.parseInt(countryData.getCurrencyPrecision()),RoundingMode.HALF_DOWN);
		if(limitAmountBd.compareTo(messgaeAmountBd1) >=0) {
			limitRepo.updateOrInsert(countryData, message.getCountry(), date, messgaeAmountBd);			
		}else {
			int offset = limitRepo.getNextAvailableDayAfterToday(countryData,date,messgaeAmountBd,1);
			ZonedDateTime newdate = date.plusDays(offset);
			limitRepo.updateOrInsert(countryData, message.getCountry(), newdate, messgaeAmountBd);		
			//archivePayments(message,limitRepo,newdate);
		}
		return CompletableFuture.completedFuture(true);		
	}

	boolean archivePayments(Message message,TransactionDao limitRepo, ZonedDateTime newdate) {

		ArchivedPayments archivepayments =  new ArchivedPayments();
		archivepayments.setCountry(message.getCountry());
		archivepayments.setDayOfYear(String.valueOf(newdate.getDayOfYear()));
		archivepayments.setMessage(message);
		archivepayments.setTimestamp(newdate.toString());
		return  limitRepo.archivePayments(archivepayments);		
	}

	public void getArchivePaymentForCountry(String country) {

	}

	public void refreshValues(CountryThrottlingData data, Map<String, Object> mapdata) {
		data.setStartTime(String.valueOf(mapdata.get("startDateTime")));
		data.setEndTime(String.valueOf(mapdata.get("endDateTime")));
		data.setAmountLimit(String.valueOf(mapdata.get("throttlingAmount")));
		data.setAmountLimit(String.valueOf(mapdata.get("throttlingAmount")));
		data.setBusinessDate(String.valueOf(mapdata.get("isBusinessDate")));
		data.setNextBusinessDate(String.valueOf(mapdata.get("nextBusinessDate")));
	}

	public String getAmount(Message m,CountryThrottlingData staticData) {
		BigDecimal bd = new BigDecimal(m.getAmount());
		BigDecimal nbd = bd.setScale(Integer.parseInt(staticData.getCurrencyPrecision()), RoundingMode.HALF_DOWN);
		return String.valueOf(nbd);
	}

	public boolean persistDailyTransactions(Message m,CountryThrottlingData staticData, TransactionDao repo) {

		boolean success = false;
		try {
			CountryStagingData messageData = CountryDbClasses.getStagingClazz(staticData.getCountry()).newInstance();			
			ZonedDateTime dateTime = getCountryTimeOf(m.getDate(),staticData);		
			messageData.setAmount(getAmount(m,staticData));
			messageData.setCountry(m.getCountry());
			messageData.setMessage(m.getContent());
			messageData.setReceivedDateTime(dateTime.toLocalDateTime().toString());
			messageData.setDayOfYear(dateTime.getDayOfYear());
			messageData.setTimeZone(staticData.getZoneId());
			success = repo.persistDailyTransactions(messageData);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return success;

	}

	public HttpHeaders createHeaders(String username, String password){
		return new HttpHeaders() {{
			String auth = username + ":" + password;
			byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")) );
			String authHeader = "Basic " + new String( encodedAuth );
			set( "Authorization", authHeader );
		}};		
	}
}