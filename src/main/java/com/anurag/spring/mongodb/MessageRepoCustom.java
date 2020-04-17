package com.anurag.spring.mongodb;

import java.util.List;

public interface MessageRepoCustom {

	List<Message> getFirstNamesLike(String firstName);

}
