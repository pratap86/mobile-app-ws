package com.pratap.app.ws;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pratap.app.ws.security.AppProperties;

@SpringBootApplication
public class MobileAppWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MobileAppWsApplication.class, args);
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
	
	@Bean
	public AppProperties getAppProperties() {
		return new AppProperties();
	}
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}
}
