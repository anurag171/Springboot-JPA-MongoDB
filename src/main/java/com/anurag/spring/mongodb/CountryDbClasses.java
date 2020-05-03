package com.anurag.spring.mongodb;

import java.util.HashMap;
import java.util.Map;
import com.anurag.spring.mongodb.staging.*;

public class CountryDbClasses {

	private static Map<String,Class<?>> _map = new HashMap<>(); 

	private static Map<String,String> _collectionmap = new HashMap<>();
	
	private static Map<String,Class<?>> _stagingmap = new HashMap<>(); 
	
	private static Map<String,String> _stagingcollectionmap = new HashMap<>(); 
	
	


	enum Countries{
		SINGAPORE("SG",SGTransactionDailyLimit.class,"sgtxndailylimit",SGStagingData.class,"sgstagingdata"),
		THAILAND("TH",THTransactionDailyLimit.class,"thtxndailylimit",THStagingData.class,"thstagingdata"),
		INDONESIA("ID",IDTransactionDailyLimit.class,"idtxndailylimit",IDStagingData.class,"idstagingdata"),
		DEFAULT("**",ArchivedPayments.class,"archivePayments",SGStagingData.class,"sgstagingdata");

		private String countryCode;
		private Class<?> clazz;
		private String collectionName;
		private Class<?> stagingclazz;
		private String stagingCollectionName;


		Countries(String countryCode, Class<?> clazz,String collectionName,Class<?> stagingclazz,String stagingCollectionName) {
			this.setCountryCode(countryCode);
			this.setClazz(clazz);
			this.setCollectionName(collectionName);
			this.setStagingclazz(stagingclazz);
			this.setStagingCollectionName(stagingCollectionName);
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


		public Class<?> getStagingclazz() {
			return stagingclazz;
		}


		public void setStagingclazz(Class<?> stagingclazz) {
			this.stagingclazz = stagingclazz;
		}


		public String getStagingCollectionName() {
			return stagingCollectionName;
		}


		public void setStagingCollectionName(String stagingCollectionName) {
			this.stagingCollectionName = stagingCollectionName;
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
	
	
	@SuppressWarnings("unchecked")
	public static Class<CountryStagingData> getStagingClazz(String country) {
		if(_stagingmap.isEmpty()) {
			for(Countries obj :Countries.values()) {
				_stagingmap.put(obj.getCountryCode(), obj.getStagingclazz());
			}			
		}		
		return (Class<CountryStagingData>) _stagingmap.get(country);
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
				_collectionmap.put(obj.getCountryCode(), obj.getStagingCollectionName());					
			}			
		}		
		return  _collectionmap;
	}
	
	
	public static String getStagingCollection(String country) {
		if(_stagingcollectionmap.isEmpty()) {
			for(Countries obj :Countries.values()) {
				_stagingcollectionmap.put(obj.getCountryCode(), obj.getStagingCollectionName());
			}			
		}		
		return (String) _stagingcollectionmap.get(country);
	}

	public static Map<String,String> getStagingCollection() {
		if(_stagingcollectionmap.isEmpty()) {
			for(Countries obj :Countries.values()) {
				_stagingcollectionmap.put(obj.getCountryCode(), obj.getCollectionName());					
			}			
		}		
		return  _stagingcollectionmap;
	}
	
	
	



}
