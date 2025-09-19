package br.com.sankhya.usermanager;

import org.springframework.boot.SpringApplication;

public class TestUsermanagerApplication {

	public static void main(String[] args) {
		SpringApplication.from(UsermanagerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
