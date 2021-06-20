package com.digite.gitpushnotification.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DigiteProjectDetailsBean {
	
	private Integer projectId=1;
	private Logger logger =LoggerFactory.getLogger(DigiteProjectDetailsBean.class);
	
	@SuppressWarnings("deprecation")
	public JSONObject loadFileDetails(Exchange exchange) {
		String fileStr=loadFileAsString("repositoryDetails.json");
		JSONObject response=null;
		try {
			ObjectMapper mapper=new ObjectMapper();
			Map map=mapper.readValue(fileStr, Map.class);
			response=new JSONObject(map);
		}catch (Exception e) {
			System.out.println("exception occured is"+e);
		}
		try {
			if(response!=null && response.has("projectDetails") && response.getJSONArray("projectDetails").length()>0) {
				JSONArray projectDetails=response.getJSONArray("projectDetails");
				boolean repoPresent=false;
				System.out.println("repo path is"+exchange.getIn().getHeader("repoPath",String.class));
				for(int counter=0;counter<projectDetails.length();counter++) {
					System.out.println("repository url is"+projectDetails.getJSONObject(counter).getString("repositoryPath"));
					if(projectDetails.getJSONObject(counter).getString("repositoryPath").equals(exchange.getIn().getHeader("repoPath",String.class))){
						System.out.println("got the repository target project");
						repoPresent=true;
						exchange.getIn().setBody(projectDetails.getJSONObject(counter));
						return projectDetails.getJSONObject(counter);
					}
				}
				return null;
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
			//res =new JSONObject
	}

	private String loadFileAsString(String string) {
		String res=null;
		try {
			File file = ResourceUtils.getFile("classpath:repositorydetails.json");
			InputStream in= new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(in));
			String line =null;
			StringBuilder fileStr=new StringBuilder("");
			while((line=br.readLine()) != null) {
				fileStr.append(line);
			}
			res=fileStr.toString();
			System.out.println("data from file is "+res);
			br.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return res;
	}
	
	
}
