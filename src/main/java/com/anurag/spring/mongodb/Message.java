package com.anurag.spring.mongodb;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
 
public class Message implements Serializable {
 
    private static final long serialVersionUID = 1L;
     
    private Long id;
    private String content;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;
    
    private String amount;
    private String country;
 
    public Message() {
    }
 
    public Message(Long id, String content, LocalDateTime date,String country,String amount) {
        super();
        this.id = id;
        this.content = content;
        this.date = date;
        this.country =  country;
        this.amount = amount;
    }
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getContent() {
        return content;
    }
 
    public void setContent(String content) {
        this.content = content;
    }
 
    public LocalDateTime getDate() {
        return date;
    }
 
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
 
    @Override
    public String toString() {
        return "Message [id=" + id + ", content=" + content + ", date=" + date + ", country="+ country + " amount "+ amount+ "]";
    }

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}	
}
