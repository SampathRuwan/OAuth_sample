package com.example.oauth.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuthController {

	@GetMapping("/")
	public String home(){
		return "index.html";
	}
	
}
