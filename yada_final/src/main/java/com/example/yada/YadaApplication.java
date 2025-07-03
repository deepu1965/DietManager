package com.example.yada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.example.yada.config.CalorieConfig;
@Import(CalorieConfig.class) 
@SpringBootApplication
public class YadaApplication {

	public static void main(String[] args) {
		SpringApplication.run(YadaApplication.class, args);
	}

}
