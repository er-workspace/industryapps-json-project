package com.industryapps.json_partial_updates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class JsonPartialUpdatesApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsonPartialUpdatesApplication.class, args);
	}

}
