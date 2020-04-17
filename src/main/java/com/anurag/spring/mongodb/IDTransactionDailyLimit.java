package com.anurag.spring.mongodb;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@TypeAlias(value = "IDLimit_")
@Document(collection = "idtxndailylimit")
@CompoundIndex(def = "{'country': 1, 'date': 1}")
public class IDTransactionDailyLimit extends CountryTransactionDailyLimit {
	
}
