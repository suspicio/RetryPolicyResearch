package com.master.RetryPolicy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RetryPolicyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetryPolicyApplication.class, args);
	}

}
