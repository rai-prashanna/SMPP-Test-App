package com.prai.smpp_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmppApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmppApiApplication.class, args);
	}

}
//curl --header "Content-Type: application/json" --request POST  --data '{"message":"Hello world!"}' http://localhost:8080/submit-msg
