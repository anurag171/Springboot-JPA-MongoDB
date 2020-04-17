package com.anurag.spring.mongodb;

import java.util.HashMap;
import java.util.Map;

public class CountryDbClasses {

	private static Map<String,Class<?>> _map = new HashMap<>(); 

	private static Map<String,String> _collectionmap = new HashMap<>(); 


	enum Countries{
		SINGAPORE("SG",SGTransactionDailyLimit.class,"sgtxndailylimit"),
		THAILAND("TH",THTransactionDailyLimit.class,"thtxndailylimit"),
		INDONESIA("ID",IDTransactionDailyLimit.class,"idtxndailylimit"),
		DEFAULT("**",ArchivedPayments.class,"archivePayments");

		private String countryCode;
		private Class<?> clazz;
		private String collectionName;


		Countries(String countryCode, Class<?> clazz,String collectionName) {
			this.setCountryCode(countryCode);
			this.setClazz(clazz);
			this.setCollectionName(collectionName);
		}


		public String getCountryCode() {
			return countryCode;
		}


		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}


		public Class<?> getClazz() {
			return clazz;
		}


		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}


		public String getCollectionName() {
			return collectionName;
		}


		public void setCollectionName(String collectionName) {
			this.collectionName = collectionName;
		}

	}

	@SuppressWarnings("unchecked")
	public static Class<CountryTransactionDailyLimit> getClazz(String country) {
		if(_map.isEmpty()) {
			for(Countries obj :Countries.values()) {
				_map.put(obj.getCountryCode(), obj.getClazz());
			}			
		}		
		return (Class<CountryTransactionDailyLimit>) _map.get(country);
	}

	public static String getCollection(String country) {
		if(_collectionmap.isEmpty()) {
			for(Countries obj :Countries.values()) {
				_collectionmap.put(obj.getCountryCode(), obj.getCollectionName());
			}			
		}		
		return (String) _collectionmap.get(country);
	}

	public static Map<String,String> getCollection() {
		if(_collectionmap.isEmpty()) {
			for(Countries obj :Countries.values()) {
				_collectionmap.put(obj.getCountryCode(), obj.getCollectionName());					
			}			
		}		
		return  _collectionmap;
	}



}
