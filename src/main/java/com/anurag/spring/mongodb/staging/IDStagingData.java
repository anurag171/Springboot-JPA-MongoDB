package com.anurag.spring.mongodb.staging;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@TypeAlias(value = "IDStaging_")
@Document(collection = "idstagingdata")
@CompoundIndex(def = "{'country': 1, 'date': 1}")
public class IDStagingData extends CountryStagingData {
	
}
