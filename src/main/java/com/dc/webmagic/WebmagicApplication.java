package com.dc.webmagic;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.SpringApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WebmagicApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebmagicApplication.class, args);
	}

}
