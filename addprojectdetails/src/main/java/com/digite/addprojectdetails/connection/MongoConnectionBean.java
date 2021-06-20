package com.digite.addprojectdetails.connection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConnectionBean {
	
	@Bean("mongoBean")
	public MongoClient mongoConnection() {
		//new ClassPathXmlApplicationContext("application-context.xml");
		return MongoClients.create("mongodb://localhost:27017");
	}
	
	

}
