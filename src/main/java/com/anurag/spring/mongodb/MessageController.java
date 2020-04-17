package com.anurag.spring.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RestController
public class MessageController {
	
	@Autowired
	JmsTemplate template;
	
	@PostMapping(value = "/push")
	public boolean postTransaction(@RequestBody String message) {
		
		Gson gson = new Gson();
        List<Message>	 entities =  gson.fromJson(message, new TypeToken<List<Message>>(){}.getType());
        template.convertAndSend(entities);
        
		
		return false;
		
	}

}
