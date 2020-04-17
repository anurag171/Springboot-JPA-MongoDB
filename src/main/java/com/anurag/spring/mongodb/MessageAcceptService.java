package com.anurag.spring.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageAcceptService {
	
	@Autowired
	MessageRepo repository;
	
	public boolean save(Message message) {
		Message saveMsgEntry = repository.save(message);
		return saveMsgEntry != null;
	}
}
