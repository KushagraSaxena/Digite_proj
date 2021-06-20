package com.digite.gitpushnotification.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digite.gitpushnotification.bean.DigiteProjectDetailsBean;

@Component
public class FinIdByRepositoryPath extends RouteBuilder{

	@Autowired
	DigiteProjectDetailsBean digiteProjectDetailsBean;
	
	@Override
	public void configure() throws Exception {
		from("direct:checkFromFileRepo").log(LoggingLevel.INFO, "load file from config")
		.bean(digiteProjectDetailsBean,"loadFileDetails").log(LoggingLevel.INFO,"response body is ${body}")
		.to("kafka:digiteprojectdetail");
	}
	

}
