package com.hospital.doctor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient

public class DocterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocterServiceApplication.class, args);
	}

}
