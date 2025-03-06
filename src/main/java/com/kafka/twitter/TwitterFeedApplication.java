package com.kafka.twitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TwitterFeedApplication {
	public static void main(String[] args) {
		SpringApplication.run(TwitterFeedApplication.class, args);
	}

}
