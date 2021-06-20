package com.digite.gitpushnotification.route;


import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.digite.gitpushnotification.model.GitRepositoryWrapper;
import com.google.gson.Gson;

@Component
public class GitPushNotificationRequest extends RouteBuilder {

	CamelContext context;
	@Override
	public void configure() throws Exception {
		context=getContext();
		context.setStreamCaching(true);
		
		from("jetty:http://127.0.0.1:8190/gitpushnotification?httpMethodRestrict=POST").log(LoggingLevel.INFO, "gitpushnotification request recieved is ${body}")
		.convertBodyTo(String.class)
		//.unmarshal(GitRepositoryWrapper.class)
		.process(new GitNotificationProcessor());
		
	}

}

@Component
class GitNotificationProcessor implements Processor {
	
	ProducerTemplate producerTemplate;

	private Logger logger =LoggerFactory.getLogger(GitNotificationProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("recieved request at processor with body :"+exchange.getIn().getBody());
		GitRepositoryWrapper gitRepositoryWrapper= parseRequestRecieved(exchange.getIn().getBody());
		if(gitRepositoryWrapper !=null && !gitRepositoryWrapper.getRepoPath().isEmpty()) {
			checkDetailsFromRepositoryPage(exchange, gitRepositoryWrapper);
		}
	}

	private void checkDetailsFromRepositoryPage(Exchange exchange, GitRepositoryWrapper gitRepositoryWrapper) {
		producerTemplate=getProducerTemplate(exchange);
		System.out.println(producerTemplate);
		exchange.getIn().setHeader("repoPath", gitRepositoryWrapper.getRepoPath());
		exchange.getIn().setHeader("projectName", gitRepositoryWrapper.getProjectName());
		producerTemplate.send("direct:checkFromFileRepo", exchange);
	}

	private ProducerTemplate getProducerTemplate(Exchange exchange) {
		if(producerTemplate==null) {
			producerTemplate=exchange.getContext().createProducerTemplate();
		}
		return producerTemplate;
	}

	private GitRepositoryWrapper parseRequestRecieved(Object body) {
		Gson gson = new Gson();
		GitRepositoryWrapper response = gson.fromJson(body.toString(), GitRepositoryWrapper.class);
		System.out.println("Translated text: " + response.getProjectName());
		return response;
	}
	
}
