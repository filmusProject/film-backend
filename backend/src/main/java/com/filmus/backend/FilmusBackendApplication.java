package com.filmus.backend;

import com.filmus.backend.movie.external.NlpEc2Manager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(NlpEc2Manager.Ec2Props.class)
public class FilmusBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilmusBackendApplication.class, args);
	}
}