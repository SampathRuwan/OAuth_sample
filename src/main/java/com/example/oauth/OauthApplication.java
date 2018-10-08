package com.example.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class OauthApplication extends SpringBootServletInitializer {

//	public static void main(String[] args) {
//		SpringApplication.run(OauthApplication.class, args);
//	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OauthApplication.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(OauthApplication.class, args);
	}
}
