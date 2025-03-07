package com.rhkr8521.ancmobility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AncmobilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(AncmobilityApplication.class, args);
	}

}
