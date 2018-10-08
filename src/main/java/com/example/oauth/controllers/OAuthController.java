package com.example.oauth.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.oauth.models.Photo;
import com.example.oauth.services.OauthService;

@Controller
public class OAuthController {

	ArrayList<Photo> photoLinkList = new ArrayList<>();
	OauthService serviceObj = new OauthService();
	
	@GetMapping("/")
	public String home(){
		return "index";
	}
	
	@GetMapping("/redirect")
	public void getAuthCode(HttpServletResponse httpServletResponse) {
    	
		//making url
	    	   String AUTH_ENDPOINT = "https://www.facebook.com/dialog/oauth";
	    	   String RESPONSE_TYPE = "code";
	    	   String CLIENT_ID = "111764886375531";
	    	   String REDIRECT_URI = "http://localhost:8080/callback";
	    	   String SCOPE = "public_profile user_posts user_friends user_photos";
	    	   
	    	   String requestEndpoint = null;
			try {
				requestEndpoint = AUTH_ENDPOINT + "?" +
				            "response_type=" + RESPONSE_TYPE + "&" +
				            "client_id=" + CLIENT_ID + "&" +
				            "redirect_uri=" + URLEncoder.encode(REDIRECT_URI,"UTF-8" ) + "&" +
				            "scope=" + URLEncoder.encode(SCOPE,"UTF-8" );
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
			//send GET request
	        httpServletResponse.setHeader("Location", requestEndpoint);
	        httpServletResponse.setStatus(302);
	    }
	
	@GetMapping("callback")
	public String getAccessToken(@RequestParam("code") String token,Model model) throws ClientProtocolException, IOException, UnsupportedOperationException, JSONException{
    	
    	//making url
	    	final String TOKEN_ENDPOINT = "https://graph.facebook.com/oauth/access_token";
            final String GRANT_TYPE = "authorization_code";
            final String REDIRECT_URI = "http://localhost:8080/callback";
            final String CLIENT_ID = "111764886375531";
            final String CLIENT_SECRET = "6baeedb54cafabc7ad273a70bb82fb7c";
	    	
            HttpPost httpPost = new HttpPost(TOKEN_ENDPOINT +
            		"?grant_type=" + GRANT_TYPE +
                    "&code=" + token +
                    "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI,"UTF-8") +
                    "&client_id=" + CLIENT_ID);
            
            //encode app id and app secret
            String clientCredentials = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedClientCredentials = new String(Base64.encodeBase64(clientCredentials.getBytes()));
            //set Authorization header
            httpPost.setHeader("Authorization", "Basic " +encodedClientCredentials);
            
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //send POST request
            HttpResponse httpResponse = httpClient.execute(httpPost);

            // Handle access token response
            Reader reader = new InputStreamReader
                    (httpResponse.getEntity().getContent());
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
        
            //get access token
            final JSONObject obj = new JSONObject(line);
            String accessToken = obj.getString("access_token");
            
            //get albums data
    		JSONObject albums = new JSONObject(serviceObj.getResourceData("https://graph.facebook.com/v2.8/me/albums",accessToken));
         
    		for (int i = 0; i < albums.getJSONArray("data").length(); i++) {
    			//extract each JSON object from JSON array
    		    JSONObject albumobject = albums.getJSONArray("data").getJSONObject(i);
    		    //get album id
    		    String id = albumobject.getString("id");
    		    //get images in particular album
    		    JSONObject photos = new JSONObject(serviceObj.getResourceData("https://graph.facebook.com/v2.8/"+id+"/photos",accessToken));
    		    
            	for (int p = 0; p < photos.getJSONArray("data").length(); p++) {
            		//extract each JSON object from JSON array
        		    JSONObject photos_object = photos.getJSONArray("data").getJSONObject(p);
        		    //get photo id
        		    String p_id = photos_object.getString("id");
        		   
        		    //get image url in particular image
        		    JSONObject photo_object = new JSONObject(serviceObj.getResourceData("https://graph.facebook.com/"+p_id+"?fields=images",accessToken));	    
        		    //extract image link from 'source' attribute
                    JSONObject image = photo_object.getJSONArray("images").getJSONObject(1);
        		    String photoLink = image.getString("source");
                    Photo uploadPhoto = new Photo();
                    uploadPhoto.setPhotoLink(photoLink);
                    //added links to the arraylist as UserPhoto objects
                    photoLinkList.add(uploadPhoto);
                    System.out.println(photoLinkList);
            	}              
    		}
    	//bind image url array with html page
    	model.addAttribute("photos", photoLinkList);
    	//display images.html page
  	    return "images";    
	    }
	
	
	
	
}
