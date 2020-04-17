package com.anurag.spring.mongodb;

import java.time.LocalDateTime;

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
		for(int i =0;i<30;i++) {
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"SG","1000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"SG","3000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"SG","4000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"SG","7000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"SG","7000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"SG","1000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"SG","2000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1001L, "test body", LocalDateTime.now(),"ID","3000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1002L, "test body", LocalDateTime.now(),"TH","1000"));
			_jmsTemplate.convertAndSend("jms.message.endpoint", new Message(1002L, "test body", LocalDateTime.now(),"SG","1999.67"));
		}
	}
	
	public void run(String[] args) {
		
	}

}
