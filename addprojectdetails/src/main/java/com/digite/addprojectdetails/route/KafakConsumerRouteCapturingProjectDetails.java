package com.digite.addprojectdetails.route;



import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digite.addprojectdetails.connection.AddProjectDetailsBean;


@Component
public class KafakConsumerRouteCapturingProjectDetails extends RouteBuilder{
	
	@Autowired
	ProjectDetailsProcessor projectProjectDetailsProcessor;
	
	@Autowired
	AddProjectDetailsBean addProjectDetailsBean;
	
	@Override
	public void configure() throws Exception {
		//mongoConnectionBean.mongoConnection();
		onException(RuntimeException.class).handled(true)
		.log(LoggingLevel.INFO,"request already present in mongo ${header.kafkaMessage}");
		from("kafka:digiteprojectdetail").log(LoggingLevel.INFO,"message recieved from kafka is ${body}")
		
		//.process(projectProjectDetailsProcessor)
		.to("mongodb:mongoBean?database=projectDetails&collection=project&operation=findOneByQuery")
		.log(LoggingLevel.INFO,"mongo response body is ${body}")
		.bean(addProjectDetailsBean,"setBodyFromPrevKafkaMsg")
		.choice().when(simple("${header.processReq} == true")).log("Need to Process for insert req")
		.to("mongodb:mongoBean?database=projectDetails&collection=project&operation=insert")
		.otherwise().log("No Need to Process for insert req");
	}
}

@Component
class ProjectDetailsProcessor implements Processor {
	
	ProducerTemplate producerTemplate;

	
	@Override
	public void process(Exchange exchange) throws Exception {
		String body= exchange.getIn().getBody(String.class);
		exchange.getIn().setHeader("kafkaMessage", body);
	}
	
}

