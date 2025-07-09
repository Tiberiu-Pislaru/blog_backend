package com.example.blog_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class BlogBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BlogBackendApplication.class, args);
	}
}
