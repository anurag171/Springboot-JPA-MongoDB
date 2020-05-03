package com.anurag.spring.mongodb.test;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.anurag.spring.mongodb.staging.SGStagingData;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class TestMongoOperations {
	
	MongoTemplate mongoTemplate = null;
	MongoClient mongoClient = null;

	public TestMongoOperations(String uri) {
		MongoClientURI mongoclienturi = new MongoClientURI(uri); 
		MongoDbFactory factory = new SimpleMongoDbFactory(mongoclienturi);
		mongoClient = new MongoClient(mongoclienturi);
		mongoTemplate = new MongoTemplate(factory);
	}
	
	private void readData() {
		Consumer<SGStagingData> setOne = (a) -> {
            a.setPicked(1);
            a.setProcessed(1);
            mongoTemplate.save(a);
        };
		
		Query query = new Query();
		query.addCriteria(Criteria.where("country").is("SG"));
		query.addCriteria(Criteria.where("processed").is(Integer.valueOf(0)));
		Pageable p = PageRequest.of(0, 30, Sort.by(Direction.ASC, "receivedDateTime"));
		query.with(p);
		Update update = new Update();
		update.set("processed",Integer.valueOf(1));
		update.set("picked",Integer.valueOf(1));
		
		
		List<SGStagingData> list = mongoTemplate.find(query, SGStagingData.class);
		/*BulkOperations bops = mongoTemplate.bulkOps(BulkMode.UNORDERED, SGStagingData.class);
		bops.updateMulti(query, update).execute();*/
		list.forEach(x->setOne.accept(x));
		System.out.println(list);
		
		
	}

	public static void main(String[] args) {
		TestMongoOperations operation =  new TestMongoOperations("mongodb://root:root@localhost:27017/admin");
		operation.readData();

	}

}
