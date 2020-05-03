package com.anurag.spring.mongodb;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccessingDataMongodbApplication {	    

	public static void main(String[] args) {
		ConfigurableApplicationContext _context = SpringApplication.run(AccessingDataMongodbApplication.class, args);
		JmsTemplate _jmsTemplate =  _context.getBean(JmsTemplate.class);		
		Random rand = new Random(); 
		for(int i =0;i<3000;i++) {			  
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"SG",String.valueOf(rand.nextDouble()*1000)));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"ID",String.valueOf(rand.nextDouble()*1000)));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1002L, "test body", LocalDateTime.now(),"TH",String.valueOf(rand.nextDouble()*1000)));
		}
	}
	
	public void run(String[] args) {
		
	}

}
