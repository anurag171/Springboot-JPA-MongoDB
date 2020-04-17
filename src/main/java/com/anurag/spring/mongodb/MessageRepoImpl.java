package com.anurag.spring.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class MessageRepoImpl implements MessageRepoCustom {
	
    @Override
    public List<Message> getFirstNamesLike(String firstName) {

    	List<Message> list = new ArrayList<>();
        return list;
    }
}