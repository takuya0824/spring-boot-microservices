package com.micro.author_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.micro.shared.errors.RestResponseEntityExceptionHandler;

@SpringBootApplication
@Import({ RestResponseEntityExceptionHandler.class })
public class AuthorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorApiApplication.class, args);
	}

}
