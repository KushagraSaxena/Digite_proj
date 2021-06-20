package com.digite.retrieveprojectdetails.route;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.tomcat.util.json.JSONParser;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.ajax.JSONPojoConvertorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


@Component
public class RetrieveProjectDetailsRoute extends RouteBuilder {

	CamelContext context;
	
	@Override
	public void configure() throws Exception {
		context=getContext();
		context.setStreamCaching(true);
		from("jetty:http://127.0.0.1:8191/retrieveprojectdetails?httpMethodRestrict=POST").log(LoggingLevel.INFO, "gitpushnotification request recieved is ${body}")
		.to("mongodb:mongoBean?database=projectDetails&collection=project&operation=findOneByQuery")
		.process(new RetrieveProjectDetailsProcessor())
		.log(LoggingLevel.INFO,"response body is ${body}");
		
	}
	
		

}
@Component
class RetrieveProjectDetailsProcessor implements Processor {

	private Logger logger =LoggerFactory.getLogger(RetrieveProjectDetailsProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String mongoResp=exchange.getIn().getBody(String.class);
		logger.info("mongo body is "+mongoResp);
		if(mongoResp !=null && mongoResp.contains("Document")) {
			mongoResp=mongoResp.replace("Document{{","{\"").replace("}}", "\"}").replace("=", "\":\"").replace(",", "\",\"");
			logger.info("mongoRespAfter replace is "+mongoResp);
			 
			JSONObject jsonObject=new JSONObject();
			try {
				ObjectMapper mapper=new ObjectMapper();
				Map map=mapper.readValue(mongoResp, Map.class);
				jsonObject=new JSONObject(map);
			}catch (Exception e) {
				System.out.println("exception occured is"+e);
			}
			
			jsonObject.put("message", "success");
			//jsonObject.put("response", json);
			exchange.getOut().setBody(jsonObject);
		}else {
			throw new RuntimeException();
		}
	}
}
