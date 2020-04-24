package com.anurag.spring.mongodb;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TaskScheduler {

	Logger logger = LoggerFactory.getLogger(TaskScheduler.class);


	@Autowired private CountryThrottlingRepo countryrepo;


	@Autowired
	Environment env;

	final String preloadFilePath = "preloadFilePath";

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	ServiceUtil util;

	@Autowired
	ApplicationContext _context;

	@Autowired
	TransactionDao dao;

	@Autowired
	RestTemplate restTemplate;

	@Value(value = "http://localhost:8080/api/country/%s")
	String uritemplate;

	@Scheduled(cron = "0/60 * * * * ?")
	// @Scheduled(fixedRate = 120000, initialDelay = 20000)
	public boolean reload() throws IOException {

		logger.error("#############START RELOAD###############" + LocalDateTime.now());
		try {
			List<CountryThrottlingData> dbentities = countryrepo.findAll();
			@SuppressWarnings("unchecked")
			Map<String,CountryThrottlingData> dataMap = _context.getBean("throttlingBean",Map.class);
			ObjectMapper mapper = new ObjectMapper();
			for(CountryThrottlingData data:dbentities) {
				String uri =  String.format(uritemplate, data.getCountry());
				String result = restTemplate.getForObject(uri, String.class);
				Map<String,Object> mapdata = mapper.readValue(result,new com.fasterxml.jackson.core.type.TypeReference<Map<String,Object>>(){ });
				util.refreshValues(data, mapdata);
				dataMap.put(data.getCountry(), data);
				System.out.println(data);
			}
			countryrepo.saveAll(dbentities);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		logger.error("#############END RELOAD###############" + LocalDateTime.now());
		return true;

	}


	@Scheduled(cron = "0/360 * * * * ?")
	public void processArchivePayments() throws IOException {
		logger.error("#############START Unarchival###############" + LocalDateTime.now());
		@SuppressWarnings("unchecked")
		Map<String, CountryThrottlingData> dataMap = _context.getBean("throttlingBean", Map.class);
		dataMap.forEach((k, v) -> dao.getArchivePayments(k, v));
	}

}
