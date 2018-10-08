package com.example.oauth.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OauthService {

	//get user resources
    public String getResourceData(String url,String accessToken) throws IOException{
    	
    		URL urlobj = new URL(url);
    		HttpURLConnection con = (HttpURLConnection) urlobj.openConnection();
    		con.setRequestMethod("GET");

    		//add request header with access token
    		con.setRequestProperty("Authorization", "Bearer " + accessToken);

    		int responseCode = con.getResponseCode();
    		System.out.println("\nSending 'GET' request to URL : " + url);
    		System.out.println("Response Code : " + responseCode);

    		BufferedReader in = new BufferedReader(
    		        new InputStreamReader(con.getInputStream()));
    		String inputLine;
    		StringBuffer response = new StringBuffer();

    		while ((inputLine = in.readLine()) != null) {
    			response.append(inputLine);
    		}
    		in.close();
    		//return JSON data
    		return response.toString();
    }
}
