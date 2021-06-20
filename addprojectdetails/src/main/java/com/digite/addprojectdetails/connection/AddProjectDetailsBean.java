package com.digite.addprojectdetails.connection;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AddProjectDetailsBean {

	private Logger logger =LoggerFactory.getLogger(AddProjectDetailsBean.class);
	
	public void setBodyFromPrevKafkaMsg(Exchange exchange) {
		logger.info("body is "+exchange.getIn().getBody().toString());
		String mongoResp=exchange.getIn().getBody(String.class);
		logger.info("mongo body is "+mongoResp);
		if(mongoResp !=null && mongoResp.contains("Document")) {
			exchange.getIn().setHeader("processReq", false);
			throw new RuntimeException();
		}else {
			exchange.getIn().setBody(exchange.getIn().getHeader("kafkaMessage"));
		}
	}
	
}
