package com.anurag.spring.mongodb;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class TaskScheduler {

	Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

	@Autowired
	private CountryThrottlingRepo countryrepo;

	@Autowired
	Environment env;

	final String preloadFilePath = "preloadFilePath";

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	ApplicationContext _context;

	@Autowired
	TransactionDao dao;

	 @Scheduled(cron = "0/300 * * * * ?" ,zone = "GMT+5:00")
	// @Scheduled(fixedRate = 120000, initialDelay = 20000)
	public boolean reload() throws IOException {

		logger.error("#############START RELOAD###############" + LocalDateTime.now());
		StringBuilder contentBuilder = new StringBuilder();
		countryrepo.deleteAll();
		File resource = resourceLoader.getResource(env.getProperty(preloadFilePath)).getFile();
		try (Stream<String> stream = Files.lines(Paths.get(resource.getPath()), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
			System.out.println(contentBuilder.toString());
			Gson gson = new Gson();
			List<CountryThrottlingData> entities = gson.fromJson(contentBuilder.toString(),
					new TypeToken<List<CountryThrottlingData>>() {
			}.getType());
			countryrepo.saveAll(entities);
			@SuppressWarnings("unchecked")
			Map<String, CountryThrottlingData> dataMap = _context.getBean("throttlingBean", Map.class);
			for (CountryThrottlingData data : entities) {
				dataMap.put(data.getCountry(), data);
			}


			JmsTemplate _jmsTemplate = _context.getBean(JmsTemplate.class);

			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L,
					"test body", LocalDateTime.now(),"SG","1000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L,
					"test body", LocalDateTime.now(),"SG","3000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L,
					"test body", LocalDateTime.now(),"SG","4000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L,
					"test body", LocalDateTime.now(),"SG","7000"));


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
