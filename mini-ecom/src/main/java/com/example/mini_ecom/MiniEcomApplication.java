package com.example.mini_ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MiniEcomApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniEcomApplication.class, args);
	}

}
