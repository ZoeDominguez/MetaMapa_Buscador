package com.metamapa.buscador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BuscadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuscadorApplication.class, args);
	}

}
