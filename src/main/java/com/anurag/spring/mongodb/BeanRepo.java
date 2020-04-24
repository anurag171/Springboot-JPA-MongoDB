package com.anurag.spring.mongodb;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Configuration
@EnableJms
@EnableAsync
public class BeanRepo {

	@Autowired
	private CountryThrottlingRepo countryrepo;
	
	@Autowired
	private TransactionDao dao;

	@Value(value = "classpath:/data.json")
	String preloadFilePath;

	@Autowired
	ResourceLoader resourceLoader;	

	@Autowired
	ApplicationContext context;

	@PostConstruct
	public boolean  preload() throws IOException {
		System.err.println("Inside preload");
		StringBuilder contentBuilder = new StringBuilder();
		countryrepo.deleteAll();
		dao.flushConfigurationOnStartUp();
		File resource = resourceLoader.getResource(preloadFilePath).getFile();			    
		try(Stream<String> stream = Files.lines(Paths.get(resource.getPath()),StandardCharsets.UTF_8)){
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
			Gson gson = new Gson();
			List<CountryThrottlingData>	 entities =  gson.fromJson(contentBuilder.toString(), new TypeToken<List<CountryThrottlingData>>(){}.getType());
			countryrepo.saveAll(entities);
			@SuppressWarnings("unchecked")
			Map<String,CountryThrottlingData> dataMap = context.getBean("throttlingBean",Map.class);
			for(CountryThrottlingData data :entities) {
      		 dataMap.put(data.getCountry(), data);
      	  }			
		}catch(Exception e) {
			e.printStackTrace();
			return false;  
		}
		return true;
	}





	@Bean
	public JmsListenerContainerFactory<?> myFactory(
			ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) 
	{
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		//factory.setConcurrency("1-1");
		// This provides all boot's default to this factory, including the message converter
		configurer.configure(factory, connectionFactory);
		// You could still override some of Boot's default if necessary.
		return factory;
	}

	@Bean
	public MessageConverter jacksonJmsMessageConverter() 
	{
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Application-");
		executor.initialize();
		return executor;
	}
	
	@Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
          = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
          "ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

	@Bean
	public Map<String,CountryThrottlingData> throttlingBean() {
		Map<String,CountryThrottlingData> throttlingData = new HashMap<>();

		return throttlingData;
	}

}
